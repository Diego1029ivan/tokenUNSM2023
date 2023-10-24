package com.example.demo.service.impl;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.constantes.FacturaConstantes;
import com.example.demo.dao.UserDAO;
import com.example.demo.pojo.User;
import com.example.demo.security.CustomerDetailsService;
import com.example.demo.security.jwt.JwtUtil;
import com.example.demo.service.UserService;
import com.example.demo.util.FacturaUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private CustomerDetailsService customerDetailsService;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		log.info("Registro interno de un usuario {}",requestMap);
		try {
				if(validateSignUpMap(requestMap)) {
					User user = userDAO.findByEmail(requestMap.get("email")); //si existe en la base de datos
					
					if(Objects.isNull(user)) {
						userDAO.save(getUserFromMap(requestMap));
						System.out.println("Dentro de saveUser");
						return FacturaUtils.getResponseEntity("Usuario registrado con éxisto", HttpStatus.CREATED);//validación
					}else {
						return FacturaUtils.getResponseEntity("El usuario ya existe con ese email", HttpStatus.BAD_REQUEST);
					}
				}else {
					return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private Boolean validateSignUpMap(Map<String, String> requestMap) {
		if(requestMap.containsKey("nombre") && requestMap.containsKey("numeroDeContacto") && requestMap.containsKey("email") && requestMap.containsKey("password")) {
			return true;
		}
		return false;
	}
	private User getUserFromMap(Map<String, String> requestMap) { //recibir el usuario del MAP
		User user = new User();
		user.setNombre(requestMap.get("nombre"));
		user.setNumberoDeContacto(requestMap.get("numeroDeContacto"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		log.info("Dentro del login");
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
			if(authentication.isAuthenticated()) {
				if(customerDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) { //estado true del status
					return new ResponseEntity<String>("{\"token\":\""+jwtUtil.generateToken(customerDetailsService.getUserDetail().getEmail(),customerDetailsService.getUserDetail().getRole())+"\"}",
							HttpStatus.OK);
				}else {
					return new ResponseEntity<String>("{\"mensaje\":\""+" Espere la aprobación del administrador"+"\"}",HttpStatus.BAD_REQUEST);
				}
				
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("{}",e);
		}
		return new ResponseEntity<String>("{\"mensaje\":\""+"credenciales incorrectas"+"\"}",HttpStatus.BAD_REQUEST);
	}
}
