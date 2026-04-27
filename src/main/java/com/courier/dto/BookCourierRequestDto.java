package com.courier.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BookCourierRequestDto {

    @NotBlank(message = "Sender username is required")
    public String  senderUsername;

    @NotBlank(message = "Receiver name is required")
    public String  receiverName;

    @NotBlank(message = "Destination is required")
    public String  destination;

    // FIX: Added @NotNull + @Min so backend rejects 0/null pincode with a clear message
    @NotNull(message = "Destination pincode is required")
    @Min(value = 100000, message = "Pincode must be a valid 6-digit number")
    public Integer destinationPincode;

    // FIX: Added @NotNull + @Positive so backend rejects 0/negative weight
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than 0")
    public Double  weight;

    @NotBlank(message = "Payment method is required")
    public String  paymentMethod;  // UPI | CARD | COD

    public String upiId;      // required when paymentMethod=UPI
    public String cardNumber; // required when paymentMethod=CARD
    public String cvv;        // required when paymentMethod=CARD

    public String  getSenderUsername()     { return senderUsername; }
    public String  getReceiverName()       { return receiverName; }
    public String  getDestination()        { return destination; }
    public Integer getDestinationPincode() { return destinationPincode; }
    public Double  getWeight()             { return weight; }
    public String  getPaymentMethod()      { return paymentMethod; }
    public String  getUpiId()              { return upiId; }
    public String  getCardNumber()         { return cardNumber; }
    public String  getCvv()               { return cvv; }

    public void setSenderUsername(String s)      { senderUsername = s; }
    public void setReceiverName(String r)        { receiverName = r; }
    public void setDestination(String d)         { destination = d; }
    public void setDestinationPincode(Integer p) { destinationPincode = p; }
    public void setWeight(Double w)              { weight = w; }
    public void setPaymentMethod(String pm)      { paymentMethod = pm; }
    public void setUpiId(String u)               { upiId = u; }
    public void setCardNumber(String c)          { cardNumber = c; }
    public void setCvv(String c)                 { cvv = c; }
}
