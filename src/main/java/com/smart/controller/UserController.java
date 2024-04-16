package com.smart.controller;

import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	
	//method for hiding common data to response
	@ModelAttribute
	public void addCommanData(Model model,Principal principal) {
		
		String userName = principal.getName();
		System.out.println("Username: "+userName);
		
		User user = userRepository.getUserByUserName(userName);
		
		System.out.println("User:"+user);
		
		model.addAttribute("user", user);
		
	}
	
	//dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form controller
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title", "Add Contact");
		 
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,/*@RequestParam("image") MultipartFile file*/Principal principal,HttpSession session) {
		
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			
			
//			//processing and uploading file
//			
//			if(file.isEmpty()) {
//				
//				System.out.println("Image is empty");
//				contact.setImage("contact.png");
//				
//			}else {
//				
//				contact.setImage(file.getOriginalFilename());
//				
//				File saveFile = new ClassPathResource("static/img").getFile();
//				
//				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
//			
//				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//				
//				System.out.println("Images is uploded");
//			}
			
			
			
			user.getContact().add(contact);
			
			contact.setUser(user);
			
			
			
			
			this.userRepository.save(user);
			
			
			
			System.out.println("DATA"+contact);
			
			System.out.println("Added to database");
		
			//message success
			
			session.setAttribute("message", new Message("Your Contact is added!! Add more..","success"));
			
			
		} catch (Exception e) {
			System.out.println("Error"+e.getMessage());
			e.printStackTrace();
			
			session.setAttribute("message", new Message("Somthing went wrong!! Try Again..","danger"));
		}
		return "normal/add_contact_form";
	}
	
	//show contact handler
	//per page=5[n]
	//current page=0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal) {
		
		m.addAttribute("title", "Show User Contacts");
		
		String userName=principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		PageRequest pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),pageable);
		
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		
		m.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	@GetMapping("/{cId}/contact")
	public String showContactsDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		
		System.out.println("CID "+cId);
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		//
		String UserName = principal.getName();
		User user = this.userRepository.getUserByUserName(UserName);
		
		if(user.getId()==contact.getUser().getId()) {
			
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		
		return "normal/contact_detail";
	}
	
	
	//delete contact handler
	@GetMapping("/delete{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId,Model model,Principal principal,HttpSession session) {
		
		Contact contact = this.contactRepository.findById(cId).get();
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		
		//check
			
		user.getContact().remove(contact);
		
		this.userRepository.save(user);
			
		System.out.println("Deleted");
		session.setAttribute("message", new Message("Contact deleted Successfully..","success"));
		
		return "redirect:/user/show-contacts/0";
	}
	
	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model m) {
		
		m.addAttribute("title","Update_contact");
		
		Contact contact = this.contactRepository.findById(cid).get();
		
		m.addAttribute("contact", contact);
		
		
		return "normal/update_form";
	}
	
	//update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,Model m,HttpSession session,Principal principal) {
		try {
			Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();
			
			User user=this.userRepository.getUserByUserName(principal.getName());
			
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated!!","success"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("CONTACT NAME "+contact.getName());
		System.out.println("CONTACT ID "+contact.getcId());
		
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	// your profile 
	@GetMapping("/profile")
	public String yourProfile(Model m) {
		
		m.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
	
}
