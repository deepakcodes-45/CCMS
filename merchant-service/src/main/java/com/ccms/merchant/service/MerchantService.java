package com.ccms.merchant.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.merchant.dto.MerchantRequest;
import com.ccms.merchant.dto.MerchantResponse;
import com.ccms.merchant.entity.Merchant;
import com.ccms.merchant.exception.ResourceNotFoundException;
import com.ccms.merchant.repository.MerchantRepository;

@Service
@Transactional
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public MerchantResponse createMerchant(MerchantRequest request) {
        Merchant merchant = new Merchant();

        merchant.setMerchantName(request.merchantName());
        merchant.setCategory(request.category());
        merchant.setLocation(request.location());

        return toResponse(merchantRepository.save(merchant));
    }

    @Transactional(readOnly = true)
    public MerchantResponse getMerchantById(Long merchantId) {
        return toResponse(findMerchant(merchantId));
    }

    @Transactional(readOnly = true)
    public List<MerchantResponse> getAllMerchants() {
        return merchantRepository
                .findAll(Sort.by(Sort.Direction.ASC, "merchantId"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MerchantResponse updateMerchant(Long merchantId, MerchantRequest request) {
        Merchant merchant = findMerchant(merchantId);

        merchant.setMerchantName(request.merchantName());
        merchant.setCategory(request.category());
        merchant.setLocation(request.location());

        return toResponse(merchantRepository.save(merchant));
    }

    public void deleteMerchant(Long merchantId) {
        Merchant merchant = findMerchant(merchantId);

        merchantRepository.delete(merchant);
        merchantRepository.flush();
    }

    private Merchant findMerchant(Long merchantId) {
        return merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Merchant not found with ID: " + merchantId
                        )
                );
    }

    private MerchantResponse toResponse(Merchant merchant) {
        return new MerchantResponse(
                merchant.getMerchantId(),
                merchant.getMerchantName(),
                merchant.getCategory(),
                merchant.getLocation()
        );
    }
}