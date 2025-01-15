package com.smoothstack.investment_orchestrator.controller;

import com.smoothstack.investment_orchestrator.dto.PortfolioDataDTO;
import com.smoothstack.investment_orchestrator.serviceImpl.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/portfolio")
@CrossOrigin(origins = "http://localhost:5173")
public class PortfolioController {
    @Autowired
    private final PortfolioService ps;

    @Operation(summary = "Get portfolio data for a portfolio ID", description = "Retrieves portfolio data " +
            "containing stock positions for a specific portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved portfolio data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "positions": [
                                        {
                                          "ticker": "GOOGL",
                                          "totalQuantity": 50
                                        },
                                        {
                                          "ticker": "AAPL",
                                          "totalQuantity": 30
                                        }
                                      ]
                                    }
                                    """)))
    })
    @GetMapping("/data/{portfolioId}")
    public PortfolioDataDTO getPortfolioData(@PathVariable Integer portfolioId) {
        return ps.getPortfolioData(portfolioId);
    }
}