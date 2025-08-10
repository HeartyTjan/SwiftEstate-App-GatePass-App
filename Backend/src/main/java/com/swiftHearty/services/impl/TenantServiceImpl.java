//package com.swiftHearty.services.impl;
//
//import com.swiftHearty.data.repository.TenantProfileRepository;
//import com.swiftHearty.dto.request.UserLoginRequest;
//import com.swiftHearty.dto.response.CreateNewUserResponse;
//import com.swiftHearty.dto.response.UserLoginResponse;
//import com.swiftHearty.exception.ResourceNotFoundException;
//import com.swiftHearty.exception.UserAlreadyExistException;
//import com.swiftHearty.services.TenantService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TenantServiceImpl implements TenantService {
//
//    @Autowired
//    private TenantProfileRepository tenantRepository;
//
//    @Override
//    public CreateNewUserResponse registerNewTenant (CreateNewTenantRequest newTenantRequest) {
//        if(tenantExist(newTenantRequest.getEmail())){
//            throw new UserAlreadyExistException("Tenant Already exist");
//        }
//        Tenant createdTenant = TenantMapper.mapRequestToTenant(newTenantRequest);
//        tenantRepository.save(createdTenant);
//        return TenantMapper.mapToResponse("Registration Successful");
//    }
//
//    @Override
//    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
//        Tenant tenant =  findUserByEmail(userLoginRequest.getEmail());
//        boolean isSuccessful = tenant.getPassword().equals(userLoginRequest.getPassword());
//        if(!isSuccessful){
//            throw  new IllegalArgumentException("Wrong Email or Password");
//        }
//        return TenantMapper.mapToLoginResponse("Login Successfully",tenant);
//
//    }
//
//    @Override
//    public Long numberOfTenants() {
//        return tenantRepository.count();
//    }
//
//    @Override
//    public void clearTenants() {
//        tenantRepository.deleteAll();
//    }
//
//    private boolean tenantExist(String email){
//        return tenantRepository.existsTenantByEmail(email);
//
//    }
//    private Tenant findUserByEmail(String email){
//        return tenantRepository.findTenantByEmail(email)
//                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
//    }
//}
