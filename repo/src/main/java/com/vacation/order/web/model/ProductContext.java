/**
 * 
 */
package com.vacation.order.web.model;

/**
 * @author Suresh Kumar S
 *
 */
public class ProductContext implements Comparable<ProductContext>{
	private String productId;
	private String productName;
	private double score;
	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProductContext [productId=" + productId + ", productName="
				+ productName + ", score=" + score + "]";
	}
	@Override
	public int compareTo(ProductContext productContext) {
		return Double.compare(productContext.getScore(), this.getScore());
	}
}
