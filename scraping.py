#!/usr/bin/env python3
"""
Web scraping module using undetected Chrome driver
"""
import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time


def scrape_product_name(url, product_selector=None):
    """
    Scrape product name from a given URL
    
    Args:
        url (str): The URL to scrape
        product_selector (str): CSS selector for the product name element
        
    Returns:
        str: The scraped product name or None if not found
    """
    driver = None
    try:
        # Configure Chrome options
        options = uc.ChromeOptions()
        options.add_argument("--headless")  # Run in headless mode
        options.add_argument("--no-sandbox")
        options.add_argument("--disable-dev-shm-usage")
        
        # Initialize the undetected Chrome driver
        driver = uc.Chrome(options=options)
        
        # Navigate to the URL
        driver.get(url)
        
        # Wait for the page to load
        time.sleep(2)
        
        # Default selector for common e-commerce sites
        if not product_selector:
            selectors = [
                'h1[data-automation-id="product-title"]',  # Common selector
                'h1.product-title',
                'h1#product-title',
                '.product-name h1',
                '.product-title',
                'h1',  # Fallback
            ]
        else:
            selectors = [product_selector]
        
        product_name = None
        for selector in selectors:
            try:
                element = WebDriverWait(driver, 5).until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, selector))
                )
                product_name = element.text.strip()
                if product_name:
                    break
            except Exception:
                continue
        
        return product_name
        
    except Exception as e:
        print(f"Error scraping product name: {e}")
        return None
        
    finally:
        if driver:
            driver.quit()


def scrape_multiple_products(urls, product_selector=None):
    """
    Scrape product names from multiple URLs
    
    Args:
        urls (list): List of URLs to scrape
        product_selector (str): CSS selector for the product name element
        
    Returns:
        dict: Dictionary with URLs as keys and product names as values
    """
    results = {}
    
    for url in urls:
        print(f"Scraping: {url}")
        product_name = scrape_product_name(url, product_selector)
        results[url] = product_name
        time.sleep(1)  # Be polite to the server
    
    return results


if __name__ == "__main__":
    # Example usage
    test_url = "https://example.com"  # Replace with actual URL
    product_name = scrape_product_name(test_url)
    print(f"Product name: {product_name}")