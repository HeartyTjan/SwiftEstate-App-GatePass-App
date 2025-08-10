package com.swiftHearty.services.impl;

import com.swiftHearty.data.model.AccessCode;
import com.swiftHearty.data.model.VisitorPass;
import com.swiftHearty.data.repository.VisitorPassRepository;
import com.swiftHearty.dto.response.VisitorPassResponse;
import com.swiftHearty.exception.ResourceNotFoundException;
import com.swiftHearty.services.VisitorPassService;
import com.swiftHearty.utils.mappers.VisitorPassMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitorPassServiceImpl implements VisitorPassService {

    private final VisitorPassRepository visitorPassRepository;
    private final VisitorPassMapper visitorPassMapper;


    @Override
    public VisitorPass generateVisitorPass(AccessCode otp, String securityName ){
       VisitorPass visitorPass = visitorPassMapper.mapToPass(otp,securityName);
       visitorPassRepository.save(visitorPass);
       return visitorPass;
    }

    @Override
    public VisitorPassResponse retrieveVisitorPass(String passId) {
       VisitorPass foundPass = findPassById(passId);
       return visitorPassMapper.mapToResponse(foundPass);
    }

    private VisitorPass findPassById(String id){
        return visitorPassRepository.findVisitorPassById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Visitor Pass not Found or Invalid Code"));
    }

    @Override
    public VisitorPassResponse closeVisitorPass(String id) {
        VisitorPass visitorPass = findPassById(id);
        if(visitorPass.getTimeOut() != null){
            throw new IllegalArgumentException("Exited already");
        }
        visitorPass.setOpen(false);
        visitorPass.setTimeOut(LocalDateTime.now());
        visitorPassRepository.save(visitorPass);
        return  visitorPassMapper.mapToResponse(visitorPass);
    }
}
