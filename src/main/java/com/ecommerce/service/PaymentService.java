
package com.ecommerce.service;

import com.ecommerce.dto.PaymentInfoDto;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Order;
import com.ecommerce.model.Payment;
import com.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(Order order, PaymentInfoDto paymentInfo) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(paymentInfo.getPaymentMethod());
        payment.setAmount(order.getTotalPrice());
        payment.setTransactionId(generateTransactionId());
        
        // Simulate payment processing
        boolean paymentSuccessful = processPaymentWithProvider(paymentInfo);
        
        if (paymentSuccessful) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }
        
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        
        // Simulate refund processing
        boolean refundSuccessful = processRefundWithProvider(payment);
        
        if (refundSuccessful) {
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            return paymentRepository.save(payment);
        } else {
            throw new RuntimeException("Failed to process refund");
        }
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean processPaymentWithProvider(PaymentInfoDto paymentInfo) {
        // In a real application, this would integrate with a payment provider
        // For this demo, we'll just simulate a successful payment
        return true;
    }

    private boolean processRefundWithProvider(Payment payment) {
        // In a real application, this would integrate with a payment provider
        // For this demo, we'll just simulate a successful refund
        return true;
    }
}
