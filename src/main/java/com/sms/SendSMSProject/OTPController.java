package com.sms.SendSMSProject;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Controller
@RequestMapping("/otp")
public class OTPController {

    @Value("${twilio.accountSid}")
    private String twilioAccountSid;

    @Value("${twilio.authToken}")
    private String twilioAuthToken;

    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    private final Random random = new Random();

    @GetMapping("/send")
    public String sendOTP(@RequestParam("phoneNumber") String phoneNumber, Model model) {
        
    	int otp = generateRandomOTP();
     
        String message = "Your OTP is: " + otp;

        // Initialize Twilio
        Twilio.init(twilioAccountSid, twilioAuthToken);

        // Send the OTP via SMS
        Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(twilioPhoneNumber), message).create();

        // Store the OTP (in-memory or database) for later verification
        // For simplicity, we'll just store it in a session attribute here
        model.addAttribute("otp", otp);

        return "otp_form";
    }

    @PostMapping("/verify")
    public String verifyOTP(@RequestParam("userOTP") String enteredOTP, @ModelAttribute("otp") String storedOTP) {
    	
        if (enteredOTP.equalsIgnoreCase(storedOTP)) {
            return "otp_success";
        } else {
            return "otp_failure";
        }
    }

    private int generateRandomOTP() {
        return random.nextInt(900000) + 100000; // Generate a 6-digit OTP
    }
}
