/**
 * 
 */
package com.vacation.order.wrapper;

import java.util.List;

import com.vacation.order.model.Product;

/**
 * @author Suresh Kumar S
 *
 */
public class ProductWrapper {
	private List<Product> products;

	/**
	 * @return the products
	 */
	public List<Product> getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
