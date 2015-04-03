/**
 * 
 */
package com.vacation.order.wrapper;

import java.util.List;

import com.vacation.order.web.model.ProductContext;

/**
 * @author Suresh Kumar S
 *
 */
public class ProductContextWrapper {
	private List<ProductContext> products;

	/**
	 * @return the products
	 */
	public List<ProductContext> getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(List<ProductContext> products) {
		this.products = products;
	}
}
