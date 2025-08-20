#!/usr/bin/env python3
"""
Main script for web scraping project
"""
import sys
import json
from scraping import scrape_product_name, scrape_multiple_products


def main():
    """
    Main function to demonstrate scraping functionality
    """
    print("Web Scraping Tool")
    print("=" * 20)
    
    # Example URLs - replace with actual URLs you want to scrape
    test_urls = [
        "https://example.com/product1",
        "https://example.com/product2",
    ]
    
    print("\nScraping single product...")
    try:
        product_name = scrape_product_name(test_urls[0])
        print(f"Product name: {product_name}")
    except Exception as e:
        print(f"Error scraping single product: {e}")
    
    print("\nScraping multiple products...")
    try:
        results = scrape_multiple_products(test_urls)
        print("Results:")
        for url, name in results.items():
            print(f"  {url}: {name}")
        
        # Save results to JSON file
        with open('scraping_results.json', 'w') as f:
            json.dump(results, f, indent=2)
        print("\nResults saved to scraping_results.json")
        
    except Exception as e:
        print(f"Error scraping multiple products: {e}")


if __name__ == "__main__":
    main()