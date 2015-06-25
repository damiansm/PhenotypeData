/*
 * Copyright 2012 - 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mousephenotype.cda.web.controllers;

import org.mousephenotype.cda.repositories.solr.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Christoph Strobl
 */
@Controller
@Scope("prototype")
@EnableSolrRepositories(basePackages = { "org.mousephenotype.cda.repositories.solr.image" }, multicoreSupport=true)
public class ProductController {

	@Autowired
	ImageService imageService;


	@RequestMapping("/product/{id}")
	public String search(Model model, @PathVariable("id") String id, HttpServletRequest request) {
		model.addAttribute("product", imageService.findById(id));
		return "product";
	}

}
