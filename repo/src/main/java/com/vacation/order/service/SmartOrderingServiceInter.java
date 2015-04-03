/**
 * 
 */
package com.vacation.order.service;

import com.vacation.order.model.Product;

/**
 * @author Suresh Kumar S
 *
 */

public interface SmartOrderingServiceInter {
	Double getWalkScore(Product product);
	Double getAmenityScore(Product product, int genericAmenitiesCount);
}
