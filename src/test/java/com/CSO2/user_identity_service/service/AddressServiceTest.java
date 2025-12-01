package com.CSO2.user_identity_service.service;

import com.CSO2.user_identity_service.dto.request.AddressRequest;
import com.CSO2.user_identity_service.dto.response.AddressDTO;
import com.CSO2.user_identity_service.model.Address;
import com.CSO2.user_identity_service.model.User;
import com.CSO2.user_identity_service.repository.AddressRepository;
import com.CSO2.user_identity_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private User user;
    private Address address;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id("userId").email("test@example.com").build();
        address = Address.builder()
                .id("addressId")
                .userId("userId")
                .label("Home")
                .streetAddress("123 Main St")
                .city("City")
                .postalCode("12345")
                .country("Country")
                .isDefaultShipping(true)
                .isDefaultBilling(true)
                .build();
        addressRequest = new AddressRequest();
        addressRequest.setLabel("Home");
        addressRequest.setStreetAddress("123 Main St");
        addressRequest.setCity("City");
        addressRequest.setPostalCode("12345");
        addressRequest.setCountry("Country");
        addressRequest.setIsDefaultShipping(true);
        addressRequest.setIsDefaultBilling(true);
    }

    @Test
    void addAddressByEmail_ShouldReturnAddressDTO_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressDTO result = addressService.addAddressByEmail("test@example.com", addressRequest);

        assertNotNull(result);
        assertEquals("Home", result.getLabel());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void getUserAddressesByEmail_ShouldReturnListOfAddressDTO_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressRepository.findByUserId("userId")).thenReturn(Collections.singletonList(address));

        List<AddressDTO> result = addressService.getUserAddressesByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Home", result.get(0).getLabel());
    }

    @Test
    void updateAddressByEmail_ShouldReturnAddressDTO_WhenAddressExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndId("userId", "addressId")).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressDTO result = addressService.updateAddressByEmail("test@example.com", "addressId", addressRequest);

        assertNotNull(result);
        assertEquals("Home", result.getLabel());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void deleteAddressByEmail_ShouldDeleteAddress_WhenAddressExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndId("userId", "addressId")).thenReturn(Optional.of(address));

        addressService.deleteAddressByEmail("test@example.com", "addressId");

        verify(addressRepository).delete(address);
    }
}
