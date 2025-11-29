package com.CSO2.user_identity_service.controller;

import com.CSO2.user_identity_service.dto.AddressDTO;
import com.CSO2.user_identity_service.dto.AddressRequest;
import com.CSO2.user_identity_service.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.getUserAddressesByEmail(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<AddressDTO> addAddress(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddressRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddressByEmail(userDetails.getUsername(), req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @RequestBody AddressRequest req) {
        return ResponseEntity.ok(addressService.updateAddressByEmail(userDetails.getUsername(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        addressService.deleteAddressByEmail(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

}
