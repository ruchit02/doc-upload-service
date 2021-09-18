package com.topnotch.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;

import com.topnotch.demo.dtos.DocUploadResponse;
import com.topnotch.demo.models.EmployeeDetails;
import com.topnotch.demo.repositories.EmployeeDetailsRepository;
import com.topnotch.demo.services.TNPhotoDetailsService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Controller
@RequestMapping("/myapp/serviceA")
public class SampleController {
	
	@Autowired
	private TNPhotoDetailsService docService;

	@Autowired
	private EmployeeDetailsRepository repository;
	
	@Value("${com.topnotch.properties.gatewayservice.host}")
	private String GATEWAY_HOST ;
	
	@Value("${com.topnotch.properties.gatewayservice.port}")
	private String GATEWAY_PORT ;
	
	@Value("${com.topnotch.properties.gatewayservice.transferprotocol}")
	private String TRANSFER_PROTOCOL;
	
	@GetMapping("/homePage")
	public String displayHomepage(ServerHttpRequest request, Model model) {

		String authHeader = request.getHeaders().getFirst("Authorization");
		String userId = request.getHeaders().getFirst("UserId");

		if (authHeader == null || !authHeader.substring(0, 7).equals("Bearer ")) {

			return "redirect:" + TRANSFER_PROTOCOL + "://" + GATEWAY_HOST + ":" + GATEWAY_PORT + "/myapp/gateway/endpoint1";
		}

		EmployeeDetails employee = repository.findByEmail(userId);

		if (employee != null) {

			model.addAttribute("empDetails", employee);
		}
		return "home";
	}

	@PostMapping(value = "/uploadPage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String displaySpecialpage(ServerHttpRequest request, Model model,
			@RequestPart("fileToUpload") Flux<FilePart> uploadedFiles) throws InterruptedException {
		
		String userId = request.getHeaders().getFirst("UserId");

		Flux<DocUploadResponse> finalResponse = uploadedFiles.filter(file -> file != null).flatMap(file -> {
			
			return Mono.fromCallable( () -> {
				
				return docService.uploadDocument(userId, file);
			}).subscribeOn(Schedulers.boundedElastic() );
		});
		
		model.addAttribute("files", finalResponse);

		return "redirect:/myapp/gateway/endpoint3" ;
	}
}
