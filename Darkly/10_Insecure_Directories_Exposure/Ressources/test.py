import requests
from bs4 import BeautifulSoup
import urllib.parse
import sys

session = requests.Session()
visited_urls = set()
readme_found = False

def check_readme(url):
    global readme_found
    try:
        response = session.get(url)
        if response.status_code == 200:
            content = response.text
            if "flag" in content.lower(): # cherche pour le mot clé "flag"
                with open("result_scraping.txt", "a", encoding="utf-8") as file:
                    file.write(f"URL: {url}\n")
                    file.write(content + "\n")
                readme_found = True
                print(f"[+] Flag trouvé dans: {url}")
    except Exception as e:
        print(f"Erreur: {e}")

def crawl(url, depth=3):
    if depth <= 0 or url in visited_urls:
        return
    
    visited_urls.add(url)
    
    try:
        response = session.get(url)
        if response.status_code != 200:
            return
        
        soup = BeautifulSoup(response.text, "html.parser")
        links = soup.find_all("a")
        
        for link in links:
            href = link.get("href")
            if not href:
                continue
                
            full_url = urllib.parse.urljoin(url, href)
            
            if "readme" in href.lower():
                check_readme(full_url)
            else:
                crawl(full_url, depth - 1)
    except Exception as e:
        print(f"Erreur: {e}")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        start_url = sys.argv[1]
        depth = 3
        if len(sys.argv) > 2:
            try:
                depth = int(sys.argv[2])
            except:
                pass
                
        print(f"[+] Scan sur: {start_url}")
        crawl(start_url, depth)
        print(f"[+] Scan terminé.")
        
        if not readme_found:
            print("Aucun flag trouvé.")
    else:
        print("Usage: python test.py [url] [depth]")