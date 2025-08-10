package com.swiftHearty.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
@Document
@Data
public class VisitorPass {
    @Id
    private String id;
    private AccessCode accessCode;
    private boolean isOpen;
    private String securityName;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;


}
