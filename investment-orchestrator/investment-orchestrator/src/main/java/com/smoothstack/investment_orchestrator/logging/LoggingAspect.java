/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.smoothstack.investment_orchestrator.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.AccountUserController.createAccountUser(..))")
    public void logCreateAccountUser(JoinPoint joinPoint) {  // specified from logBefore
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.AccountUserController.updateAccountUserById(..))")
    public void logUpdateAccountUser(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.AccountUserController.deleteAccountUserById(..))")
    public void logDeleteAccountUser(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.AccountController.createAccount(..))")
    public void logCreateAccount(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.AccountController.updateAccountById(..))")
    public void logUpdateAccount(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.AccountController.deleteAccountById(..))")
    public void logDeleteAccount(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.AccountTransactionController.createAccountTransaction(..))")
    public void logCreateAccountTransaction(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.AccountTransactionController.updateAccountTransactionById(..))")
    public void logUpdateAccountTransaction(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.AccountTransactionController.deleteAccountTransactionById(..))")
    public void logDeleteAccountTransaction(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.InvestmentPortfolioController.createInvestmentPortfolio(..))")
    public void logCreateInvestmentPortfolio(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.InvestmentPortfolioController.updateInvestmentPortfolioById(..))")
    public void logUpdateInvestmentPortfolio(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.InvestmentPortfolioController.deleteInvestmentPortfolioById(..))")
    public void logDeleteInvestmentPortfolio(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.InvestmentPortfolioTransactionController.createInvestmentPortfolioTransaction(..))")
    public void logCreateInvestmentPortfolioTransaction(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.InvestmentPortfolioTransactionController.updateInvestmentPortfolioTransactionById(..))")
    public void logUpdateInvestmentPortfolioTransaction(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.InvestmentPortfolioTransactionController.deleteInvestmentPortfolioTransactionById(..))")
    public void logDeleteInvestmentPortfolioTransaction(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.InvestmentInvestor.createInvestor(..))")
    public void logCreateInvestor(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.InvestmentInvestor.updateInvestorById(..))")
    public void logUpdateInvestor(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.InvestmentInvestor.deleteInvestorById(..))")
    public void logDeleteInvestor(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.PositionController.createPosition(..))")
    public void logCreatePosition(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.PositionController.updatePositionById(..))")
    public void logUpdatePosition(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.PositionController.deletePositionById(..))")
    public void logDeletePosition(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.PositionTransactionController.createPositionTransaction(..))")
    public void logCreatePositionTransaction(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.PositionTransactionController.updatePositionTransactionById(..))")
    public void logUpdatePositionTransaction(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.PositionTransactionController.deletePositionTransactionById(..))")
    public void logDeletePositionTransaction(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    @Before("execution(* com.smoothstack.investment_orchestrator.controller.PositionController.tradeStock(..))")
    public void logTradeStock(JoinPoint joinPoint) {
        logCommon(joinPoint);
    }

    private void logCommon(JoinPoint joinPoint) {
        String logMessage = String.format("Request received at %s: %s%nUser Identity: %s%nMethod: %s.%s()%nRequest Parameters: %s",
                LocalDateTime.now(), getRequestURL(), getCurrentUser(),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));

        logger.info(logMessage);
        FileLogger.logToFile(logMessage);
    }

    private String getRequestURL() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getRequestURL().toString();
    }

    private String getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("userId");  // what else do we want to extract for the logging?
    }
}