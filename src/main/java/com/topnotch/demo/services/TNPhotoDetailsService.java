package com.topnotch.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.topnotch.demo.dtos.DocUploadResponse;
import com.topnotch.demo.dtos.EmployeeDetailsDTO;
import com.topnotch.demo.models.EmployeeDetails;
import com.topnotch.demo.models.EmployeeDocuments;
import com.topnotch.demo.repositories.EmployeeDetailsRepository;
import com.topnotch.demo.repositories.EmployeeDocumentsRepository;

@Service
public class TNPhotoDetailsService {

	@Autowired
	private EmployeeDetailsRepository employeeDetailsRepository;

	@Autowired
	private EmployeeDocumentsRepository employeeDocumentsRepository;

	public void createEmployeeAcc(EmployeeDetailsDTO employeeDetailsDTO) {

		System.out.println("Mapping credentials to database object ....");

		EmployeeDetails employee = new EmployeeDetails();

		employee.setFirst_name(employeeDetailsDTO.getFirst_name());
		employee.setLast_name(employeeDetailsDTO.getLast_name());
		employee.setDepartment(employeeDetailsDTO.getDepartment());
		employee.setExpertise(employeeDetailsDTO.getExpertise());
		employee.setEmail(employeeDetailsDTO.getEmail());

		employeeDetailsRepository.saveAndFlush(employee);
		System.out.println("Object pushed to database ....");
	}

	public DocUploadResponse uploadDocument(String email, FilePart file) {

		System.out.println("Mapping doc's metadata to doc database object ....");

		String[] details = fileDetailsExtractor( file.filename() );
		
		byte[] rawData = file.content().toStream().map( buffer -> buffer.asByteBuffer().array() ).findFirst().get() ;
		
		EmployeeDocuments emp_doc = new EmployeeDocuments();
		emp_doc.setDoc_name(details[0]);
		emp_doc.setDoc_type(details[1]);
		emp_doc.setData(rawData);

		EmployeeDetails employee = employeeDetailsRepository.findByEmail(email);
		emp_doc.setEmp_id(employee);

		System.out.println("Database object created ....");

		employeeDocumentsRepository.saveAndFlush(emp_doc);

		System.out.println("Photo object pushed to database ....");

		return new DocUploadResponse(details[0], details[1]);
	}

	private String[] fileDetailsExtractor(String fileName) {

		int startIndex = fileName.lastIndexOf('.');
		int endIndex = fileName.length();

		String docName = fileName.substring(0, startIndex);
		String docType = fileName.substring(startIndex, endIndex);

		String[] details = new String[2];
		details[0] = docName;
		details[1] = docType;

		return details;
	}
}
