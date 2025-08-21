#!/usr/bin/env python3
"""
Main script for web scraping project with GUI
"""
import tkinter as tk
from tkinter import messagebox
import webbrowser
import urllib.parse
from gui import ProductSearchGUI
from scraping import scrape_product_name


def main():
    """
    Main function to run the GUI application
    """
    try:
        root = tk.Tk()
        app = ProductSearchGUI(root)
        root.mainloop()
    except Exception as e:
        print(f"Error starting application: {e}")
        messagebox.showerror("Error", f"Failed to start application: {e}")


if __name__ == "__main__":
    main()