package com.example.demo.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constantes.FacturaConstantes;
import com.example.demo.service.UserService;
import com.example.demo.util.FacturaUtils;


@RestController
@RequestMapping(path="/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping("/signup")
	public  ResponseEntity<String> registrarUsuario(@RequestBody(required=true)Map<String,String> requestMap){
		try {
			return userService.signUp(requestMap); 
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
		return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
		//Log.info("Registro interno de un usuario {}",requestMap);
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody(required=true)Map<String,String> requestMap){
		try {
			return userService.login(requestMap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
