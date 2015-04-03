/**
 * 
 */
package com.vacation.order.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vacation.order.common.SmartOrderingConstants;
import com.vacation.order.model.Product;
import com.vacation.order.service.SmartOrderingServiceInter;
import com.vacation.order.web.model.ProductContext;
import com.vacation.order.wrapper.ProductContextWrapper;
import com.vacation.order.wrapper.ProductWrapper;

/**
 * @author Suresh Kumar S
 *
 */

@RestController
//vr-pms: vacation rental property management systems
@RequestMapping(value="/vr-pms")
public class SmartOrderingController {

	@Autowired
	SmartOrderingServiceInter smartOrderingServiceInter;

	@RequestMapping(value="/smartorder", method=RequestMethod.POST,consumes="application/json",produces="application/json")
	@ResponseBody
	public ProductContextWrapper sortProductUsingType(@RequestBody ProductWrapper productWrapper) {
		return sortProduct("", productWrapper);
	}

	@RequestMapping(value="/smartorder/{type}", method=RequestMethod.POST,consumes="application/json",produces="application/json")
	@ResponseBody
	public ProductContextWrapper sortProduct(@PathVariable("type") String sortOption, @RequestBody ProductWrapper productWrapper) {
		ProductContextWrapper productContextWrapper = null;
		List<Product> products = productWrapper.getProducts();
		if(products != null && !products.isEmpty()){
			productContextWrapper = new ProductContextWrapper();
			List<ProductContext> productContexts = new ArrayList<ProductContext>();
			int genericAmenitiesCount = 0;
			if(!sortOption.equalsIgnoreCase(SmartOrderingConstants.WALKSCORE)) genericAmenitiesCount = getGenericAmenitiesCount(products);
			for (Product product : products) {
				productContexts.add(buildProductContext(product, genericAmenitiesCount, sortOption));
			}
			Collections.sort(productContexts);
			productContextWrapper.setProducts(productContexts);
		}
		return productContextWrapper;
	}

	private int getGenericAmenitiesCount(List<Product> products) {
		Set<String> amenities = new HashSet<String>();
		for (Product product : products) {
			amenities.addAll(product.getAmenities());
		}
		return amenities.size();
	}

	private ProductContext buildProductContext(Product product, int genericAmenitiesCount, String sortOption) {
		Double score = 0.0;
		if(sortOption.equalsIgnoreCase(SmartOrderingConstants.AMENITY)) score = getAmenityScore(product, genericAmenitiesCount);
		else if (sortOption.equalsIgnoreCase(SmartOrderingConstants.WALKSCORE)) score = getWalkScore(product);
		else score = getAmenityScore(product, genericAmenitiesCount) + getWalkScore(product);
		ProductContext productContext = new ProductContext();
		productContext.setProductId(product.getProductId());
		productContext.setProductName(product.getProductName());
		productContext.setScore(score);
		return productContext;
	}

	private Double getWalkScore(Product product) {
		return smartOrderingServiceInter.getWalkScore(product);
	}

	private Double getAmenityScore(Product product, int genericAmenitiesCount) {
		return smartOrderingServiceInter.getAmenityScore(product, genericAmenitiesCount);
	}
}
