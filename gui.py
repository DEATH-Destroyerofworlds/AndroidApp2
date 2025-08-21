#!/usr/bin/env python3
"""
GUI module for product search application
"""
import tkinter as tk
from tkinter import ttk, messagebox, scrolledtext
import threading
import webbrowser
import urllib.parse
from scraping import scrape_product_name, scrape_multiple_products


class ProductSearchGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Product Name Scraper")
        self.root.geometry("800x600")
        self.root.resizable(True, True)
        
        # Configure style
        style = ttk.Style()
        style.theme_use('clam')
        
        self.setup_ui()
        
    def setup_ui(self):
        """Setup the user interface"""
        # Main frame
        main_frame = ttk.Frame(self.root, padding="10")
        main_frame.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S))
        
        # Configure grid weights
        self.root.columnconfigure(0, weight=1)
        self.root.rowconfigure(0, weight=1)
        main_frame.columnconfigure(1, weight=1)
        
        # Title
        title_label = ttk.Label(main_frame, text="Product Name Scraper", 
                               font=("Arial", 16, "bold"))
        title_label.grid(row=0, column=0, columnspan=3, pady=(0, 20))
        
        # URL input section
        ttk.Label(main_frame, text="Product URL:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.url_entry = ttk.Entry(main_frame, width=60)
        self.url_entry.grid(row=1, column=1, sticky=(tk.W, tk.E), pady=5, padx=(10, 0))
        
        # CSS Selector input (optional)
        ttk.Label(main_frame, text="CSS Selector (optional):").grid(row=2, column=0, sticky=tk.W, pady=5)
        self.selector_entry = ttk.Entry(main_frame, width=60)
        self.selector_entry.grid(row=2, column=1, sticky=(tk.W, tk.E), pady=5, padx=(10, 0))
        
        # Buttons frame
        button_frame = ttk.Frame(main_frame)
        button_frame.grid(row=3, column=0, columnspan=3, pady=20)
        
        self.scrape_button = ttk.Button(button_frame, text="Scrape Product Name", 
                                       command=self.scrape_single_product)
        self.scrape_button.pack(side=tk.LEFT, padx=(0, 10))
        
        self.clear_button = ttk.Button(button_frame, text="Clear Results", 
                                      command=self.clear_results)
        self.clear_button.pack(side=tk.LEFT)
        
        # Results section
        ttk.Label(main_frame, text="Results:", font=("Arial", 12, "bold")).grid(
            row=4, column=0, sticky=tk.W, pady=(20, 5))
        
        # Results text area
        self.results_text = scrolledtext.ScrolledText(main_frame, height=15, width=80)
        self.results_text.grid(row=5, column=0, columnspan=3, sticky=(tk.W, tk.E, tk.N, tk.S), 
                              pady=5)
        
        # Configure grid weights for resizing
        main_frame.rowconfigure(5, weight=1)
        
        # Progress bar
        self.progress = ttk.Progressbar(main_frame, mode='indeterminate')
        self.progress.grid(row=6, column=0, columnspan=3, sticky=(tk.W, tk.E), pady=10)
        
        # Status label
        self.status_label = ttk.Label(main_frame, text="Ready to scrape")
        self.status_label.grid(row=7, column=0, columnspan=3, pady=5)
        
    def scrape_single_product(self):
        """Scrape a single product in a separate thread"""
        url = self.url_entry.get().strip()
        if not url:
            messagebox.showerror("Error", "Please enter a URL")
            return
            
        if not url.startswith(('http://', 'https://')):
            url = 'https://' + url
            self.url_entry.delete(0, tk.END)
            self.url_entry.insert(0, url)
        
        # Start scraping in a separate thread
        threading.Thread(target=self._scrape_worker, args=(url,), daemon=True).start()
        
    def _scrape_worker(self, url):
        """Worker function for scraping (runs in separate thread)"""
        try:
            # Update UI
            self.root.after(0, self._update_status, "Scraping in progress...")
            self.root.after(0, self.progress.start)
            self.root.after(0, lambda: self.scrape_button.config(state='disabled'))
            
            # Get selector if provided
            selector = self.selector_entry.get().strip() or None
            
            # Perform scraping
            product_name = scrape_product_name(url, selector)
            
            # Update results
            result_text = f"URL: {url}\n"
            result_text += f"Product Name: {product_name or 'Not found'}\n"
            result_text += f"Timestamp: {self._get_timestamp()}\n"
            result_text += "-" * 50 + "\n\n"
            
            self.root.after(0, self._append_results, result_text)
            self.root.after(0, self._update_status, "Scraping completed!")
            
        except Exception as e:
            error_msg = f"Error scraping {url}: {str(e)}\n"
            self.root.after(0, self._append_results, error_msg)
            self.root.after(0, self._update_status, "Scraping failed!")
            
        finally:
            self.root.after(0, self.progress.stop)
            self.root.after(0, lambda: self.scrape_button.config(state='normal'))
    
    def _append_results(self, text):
        """Append text to results area"""
        self.results_text.insert(tk.END, text)
        self.results_text.see(tk.END)
        
    def _update_status(self, status):
        """Update status label"""
        self.status_label.config(text=status)
        
    def _get_timestamp(self):
        """Get current timestamp"""
        from datetime import datetime
        return datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        
    def clear_results(self):
        """Clear the results text area"""
        self.results_text.delete(1.0, tk.END)
        self.status_label.config(text="Ready to scrape")


def main():
    """Main function to run the GUI"""
    root = tk.Tk()
    app = ProductSearchGUI(root)
    root.mainloop()


if __name__ == "__main__":
    main()