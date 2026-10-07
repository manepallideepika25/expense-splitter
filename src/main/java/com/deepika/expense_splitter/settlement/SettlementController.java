package com.deepika.expense_splitter.settlement;

import com.deepika.expense_splitter.settlement.dto.CreateSettlementRequest;
import com.deepika.expense_splitter.settlement.dto.SettlementResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/settlements")
public class SettlementController {
    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping("/suggested")
    public List<SuggestedSettlement> getSuggested(@PathVariable Long groupId) {
        return settlementService.getSuggestedSettlements(groupId);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SettlementResponse recordSettlement(@PathVariable Long groupId,
                                               @Valid @RequestBody CreateSettlementRequest request) {
        return settlementService.recordSettlement(groupId, request);
    }

    @GetMapping
    public List<SettlementResponse> getSettlements(@PathVariable Long groupId) {
        return settlementService.getSettlements(groupId);
    }
}
