package com.CSO2.user_identity_service.service;

import com.CSO2.user_identity_service.dto.AddressDTO;
import com.CSO2.user_identity_service.dto.AddressRequest;
import com.CSO2.user_identity_service.model.Address;
import com.CSO2.user_identity_service.model.User;
import com.CSO2.user_identity_service.repository.AddressRepository;
import com.CSO2.user_identity_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public AddressDTO addAddress(String userId, AddressRequest req) {
        return createAddress(userId, req);
    }

    public AddressDTO addAddressByEmail(String email, AddressRequest req) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return createAddress(user.getId(), req);
    }

    private AddressDTO createAddress(String userId, AddressRequest req) {
        Address address = Address.builder()
                .userId(userId)
                .label(req.getLabel())
                .streetAddress(req.getStreetAddress())
                .city(req.getCity())
                .postalCode(req.getPostalCode())
                .country(req.getCountry())
                .isDefaultShipping(req.getIsDefaultShipping())
                .isDefaultBilling(req.getIsDefaultBilling())
                .build();

        addressRepository.save(address);
        return mapToAddressDTO(address);
    }

    public List<AddressDTO> getUserAddresses(String userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::mapToAddressDTO)
                .collect(Collectors.toList());
    }

    public List<AddressDTO> getUserAddressesByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return getUserAddresses(user.getId());
    }

    public void deleteAddress(String userId, String addressId) {
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user"));
        addressRepository.delete(address);
    }

    public void deleteAddressByEmail(String email, String addressId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        deleteAddress(user.getId(), addressId);
    }

    public AddressDTO updateAddress(String userId, String addressId, AddressRequest req) {
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user"));

        address.setLabel(req.getLabel());
        address.setStreetAddress(req.getStreetAddress());
        address.setCity(req.getCity());
        address.setPostalCode(req.getPostalCode());
        address.setCountry(req.getCountry());
        address.setIsDefaultShipping(req.getIsDefaultShipping());
        address.setIsDefaultBilling(req.getIsDefaultBilling());

        addressRepository.save(address);
        return mapToAddressDTO(address);
    }

    public AddressDTO updateAddressByEmail(String email, String addressId, AddressRequest req) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return updateAddress(user.getId(), addressId, req);
    }

    private AddressDTO mapToAddressDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .label(address.getLabel())
                .streetAddress(address.getStreetAddress())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefaultShipping(address.getIsDefaultShipping())
                .isDefaultBilling(address.getIsDefaultBilling())
                .build();
    }
}
