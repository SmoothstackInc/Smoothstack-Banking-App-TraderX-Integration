package com.smoothstack.investment_orchestrator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String createTables = """
            CREATE TABLE IF NOT EXISTS public.account (
                account_id integer NOT NULL,
                account_name character varying NOT NULL,
                account_description character varying,
                total_invested numeric NOT NULL,
                amt_available numeric NOT NULL,
                date_created timestamp without time zone NOT NULL,
                date_modified timestamp without time zone NOT NULL
            );
                        
                        
            ALTER TABLE public.account ALTER COLUMN account_id ADD GENERATED BY DEFAULT AS IDENTITY (
                SEQUENCE NAME public.account_account_id_seq
                START WITH 1
                INCREMENT BY 1
                NO MINVALUE
                NO MAXVALUE
                CACHE 1
            );
                        
                        
            CREATE TABLE IF NOT EXISTS public.account_transaction (
                account_transaction_id integer NOT NULL,
                account_id integer NOT NULL,
                account_user_id integer NOT NULL,
                funds_moved numeric NOT NULL,
                date_created timestamp without time zone NOT NULL,
                date_modified timestamp without time zone NOT NULL
            );
                        
                        
            ALTER TABLE public.account_transaction ALTER COLUMN account_transaction_id ADD GENERATED BY DEFAULT AS IDENTITY (
                SEQUENCE NAME public.account_transaction_account_transaction_id_seq
                START WITH 1
                INCREMENT BY 1
                NO MINVALUE
                NO MAXVALUE
                CACHE 1
            );
                        
                        
            CREATE TABLE IF NOT EXISTS public.account_user (
                account_user_id integer NOT NULL,
                user_id integer NOT NULL,
                user_name character varying NOT NULL,
                amt_available numeric NOT NULL,
                date_created timestamp without time zone NOT NULL,
                date_modified timestamp without time zone NOT NULL
            );
                        
                        
            ALTER TABLE public.account_user ALTER COLUMN account_user_id ADD GENERATED BY DEFAULT AS IDENTITY (
                SEQUENCE NAME public.account_user_account_user_id_seq
                START WITH 1
                INCREMENT BY 1
                NO MINVALUE
                NO MAXVALUE
                CACHE 1
            );
                        
                        
            CREATE TABLE IF NOT EXISTS public.investment_portfolio (
                investment_portfolio_id integer NOT NULL,
                investor_id integer NOT NULL,
                investment_portfolio_name character varying NOT NULL,
                total_invested numeric NOT NULL,
                amt_available numeric NOT NULL,
                date_created timestamp without time zone NOT NULL,
                date_modified timestamp without time zone NOT NULL
            );
                        
                        
            ALTER TABLE public.investment_portfolio ALTER COLUMN investment_portfolio_id ADD GENERATED BY DEFAULT AS IDENTITY (
                SEQUENCE NAME public.investment_portfolio_investment_portfolio_id_seq
                START WITH 1
                INCREMENT BY 1
                NO MINVALUE
                NO MAXVALUE
                CACHE 1
            );
                        
                        
            CREATE TABLE IF NOT EXISTS public.investment_portfolio_transaction (
                investment_portfolio_transaction_id integer NOT NULL,
                investment_portfolio_id integer NOT NULL,
                investor_id integer NOT NULL,
                funds_moved numeric NOT NULL,
                date_created timestamp without time zone NOT NULL,
                date_modified timestamp without time zone NOT NULL
            );
                        
                        
            ALTER TABLE public.investment_portfolio_transaction ALTER COLUMN investment_portfolio_transaction_id ADD GENERATED BY DEFAULT AS IDENTITY (
                SEQUENCE NAME public.investment_portfolio_transaction_investment_portfolio_transaction_id_seq
                START WITH 1
                INCREMENT BY 1
                NO MINVALUE
                NO MAXVALUE
                CACHE 1
            );
                        
                        
            CREATE TABLE IF NOT EXISTS public.investor (
                investor_id integer NOT NULL,
                user_id integer NOT NULL,
                user_name character varying NOT NULL,
                date_created timestamp without time zone NOT NULL,
                date_modified timestamp without time zone NOT NULL
            );
                        
                        
            ALTER TABLE public.investor ALTER COLUMN investor_id ADD GENERATED BY DEFAULT AS IDENTITY (
                SEQUENCE NAME public.investor_investor_id_seq
                START WITH 1
                INCREMENT BY 1
                NO MINVALUE
                NO MAXVALUE
                CACHE 1
            );
                        
                        
            CREATE TABLE IF NOT EXISTS public."position" (
                position_id integer NOT NULL,
                account_id integer,
                investment_portfolio_id integer,
                ticker character varying NOT NULL,
                security_name character varying NOT NULL,
                total_value numeric NOT NULL,
                total_quantity integer NOT NULL,
                date_created timestamp without time zone NOT NULL,
                date_modified timestamp without time zone NOT NULL
            );
                        
                        
            ALTER TABLE public."position" ALTER COLUMN position_id ADD GENERATED BY DEFAULT AS IDENTITY (
                SEQUENCE NAME public.position_position_id_seq
                START WITH 1
                INCREMENT BY 1
                NO MINVALUE
                NO MAXVALUE
                CACHE 1
            );
                        
                        
            CREATE TABLE IF NOT EXISTS public.position_transaction (
                position_transaction_id integer NOT NULL,
                position_id integer NOT NULL,
                locked_price numeric NOT NULL,
                quantity integer NOT NULL,
                side character varying NOT NULL,
                date_created timestamp without time zone NOT NULL,
                date_modified timestamp without time zone NOT NULL
            );
                        
                        
            ALTER TABLE public.position_transaction ALTER COLUMN position_transaction_id ADD GENERATED BY DEFAULT AS IDENTITY (
                SEQUENCE NAME public.position_transaction_position_transaction_id_seq
                START WITH 1
                INCREMENT BY 1
                NO MINVALUE
                NO MAXVALUE
                CACHE 1
            );                
            """;

    private final String insertData = """
            INSERT INTO public.investment_portfolio (investment_portfolio_id, investor_id, investment_portfolio_name, total_invested, amt_available, date_created, date_modified) VALUES (1, 1, 'Communications Investments', 2000.00, 1000.00, '2024-09-17 14:30:00', '2024-09-17 14:30:00') ON CONFLICT DO NOTHING;
                        
                        
            INSERT INTO public.investment_portfolio_transaction (investment_portfolio_transaction_id, investment_portfolio_id, investor_id, funds_moved, date_created, date_modified) VALUES (2, 1, 1, 1000.00, '2024-09-18 14:30:00', '2024-09-18 14:30:00') ON CONFLICT DO NOTHING;
            INSERT INTO public.investment_portfolio_transaction (investment_portfolio_transaction_id, investment_portfolio_id, investor_id, funds_moved, date_created, date_modified) VALUES (1, 1, 1, 1000.00, '2024-09-17 14:30:00', '2024-09-17 14:30:00') ON CONFLICT DO NOTHING;
                        
                        
            INSERT INTO public.investor (investor_id, user_id, user_name, date_created, date_modified) VALUES (1, 19, 'Dante DiCaprio', '2024-09-17 14:30:00', '2024-09-17 14:30:00') ON CONFLICT DO NOTHING;
                        
                        
            INSERT INTO public."position" (position_id, account_id, investment_portfolio_id, ticker, security_name, total_value, total_quantity, date_created, date_modified) VALUES (5, NULL, 1, 'DISCA', 'Discovery', 200.00, 20, '2024-09-17 14:30:00', '2024-09-17 14:30:00') ON CONFLICT DO NOTHING;
            INSERT INTO public."position" (position_id, account_id, investment_portfolio_id, ticker, security_name, total_value, total_quantity, date_created, date_modified) VALUES (4, NULL, 1, 'CMCSA', 'Comcast', 200.00, 20, '2024-09-17 14:30:00', '2024-09-17 14:30:00') ON CONFLICT DO NOTHING;
            INSERT INTO public."position" (position_id, account_id, investment_portfolio_id, ticker, security_name, total_value, total_quantity, date_created, date_modified) VALUES (3, NULL, 1, 'T', 'AT&T', 200.00, 20, '2024-09-17 14:30:00', '2024-09-17 14:30:00') ON CONFLICT DO NOTHING;
            INSERT INTO public."position" (position_id, account_id, investment_portfolio_id, ticker, security_name, total_value, total_quantity, date_created, date_modified) VALUES (2, NULL, 1, 'GOOGL', 'Alphabet', 200.00, 20, '2024-09-17 14:30:00', '2024-09-17 14:30:00') ON CONFLICT DO NOTHING;
            INSERT INTO public."position" (position_id, account_id, investment_portfolio_id, ticker, security_name, total_value, total_quantity, date_created, date_modified) VALUES (1, NULL, 1, 'ATVI', 'Activision Blizzard', 200.00, 20, '2024-09-17 14:30:00', '2024-09-17 14:30:00') ON CONFLICT DO NOTHING;
                        
                        
            INSERT INTO public.position_transaction (position_transaction_id, position_id, locked_price, quantity, side, date_created, date_modified) VALUES (5004, 5, 10.00, 20, 'BUY', '2024-09-18 14:00:00', '2024-09-18 14:00:00') ON CONFLICT DO NOTHING;
            INSERT INTO public.position_transaction (position_transaction_id, position_id, locked_price, quantity, side, date_created, date_modified) VALUES (5003, 4, 10.00, 20, 'BUY', '2024-09-18 14:00:00', '2024-09-18 14:00:00') ON CONFLICT DO NOTHING;
            INSERT INTO public.position_transaction (position_transaction_id, position_id, locked_price, quantity, side, date_created, date_modified) VALUES (5002, 3, 10.00, 20, 'BUY', '2024-09-18 14:00:00', '2024-09-18 14:00:00') ON CONFLICT DO NOTHING;
            INSERT INTO public.position_transaction (position_transaction_id, position_id, locked_price, quantity, side, date_created, date_modified) VALUES (5001, 2, 10.00, 20, 'BUY', '2024-09-18 14:00:00', '2024-09-18 14:00:00') ON CONFLICT DO NOTHING;
            INSERT INTO public.position_transaction (position_transaction_id, position_id, locked_price, quantity, side, date_created, date_modified) VALUES (5000, 1, 10.00, 20, 'BUY', '2024-09-18 14:00:00', '2024-09-18 14:00:00') ON CONFLICT DO NOTHING;
            """;

    private final String finishCreate = """
            SELECT pg_catalog.setval('public.account_account_id_seq', 1, false);
                        
                        
            SELECT pg_catalog.setval('public.account_transaction_account_transaction_id_seq', 1, false);
                        
                        
            SELECT pg_catalog.setval('public.account_user_account_user_id_seq', 1, false);
                        
                        
            ALTER TABLE ONLY public.account
                ADD CONSTRAINT account_pkey PRIMARY KEY (account_id);
                        
                        
            ALTER TABLE ONLY public.account_transaction
                ADD CONSTRAINT account_transaction_pkey PRIMARY KEY (account_transaction_id);
                        
                        
            ALTER TABLE ONLY public.account_user
                ADD CONSTRAINT account_user_pkey PRIMARY KEY (account_user_id);
                        
                        
            ALTER TABLE ONLY public.investment_portfolio
                ADD CONSTRAINT investment_portfolio_pkey PRIMARY KEY (investment_portfolio_id);
                        
                        
            ALTER TABLE ONLY public.investment_portfolio_transaction
                ADD CONSTRAINT investment_portfolio_transaction_pkey PRIMARY KEY (investment_portfolio_transaction_id);
                        
                        
            ALTER TABLE ONLY public.investor
                ADD CONSTRAINT investor_pkey PRIMARY KEY (investor_id);
                        
                        
            ALTER TABLE ONLY public."position"
                ADD CONSTRAINT position_pkey PRIMARY KEY (position_id);
                        
                        
            ALTER TABLE ONLY public.position_transaction
                ADD CONSTRAINT position_transaction_pkey PRIMARY KEY (position_transaction_id);
                        
                        
            ALTER TABLE ONLY public.account_transaction
                ADD CONSTRAINT account_fkey FOREIGN KEY (account_id) REFERENCES public.account(account_id) NOT VALID;
                        
                        
            ALTER TABLE ONLY public."position"
                ADD CONSTRAINT account_fkey FOREIGN KEY (account_id) REFERENCES public.account(account_id);
                        
                        
            ALTER TABLE ONLY public.account_transaction
                ADD CONSTRAINT account_user_fkey FOREIGN KEY (account_user_id) REFERENCES public.account_user(account_user_id) NOT VALID;
                        
                        
            ALTER TABLE ONLY public.investment_portfolio_transaction
                ADD CONSTRAINT investment_portfolio_fkey FOREIGN KEY (investment_portfolio_id) REFERENCES public.investment_portfolio(investment_portfolio_id);
                        
                        
            ALTER TABLE ONLY public."position"
                ADD CONSTRAINT investment_portfolio_fkey FOREIGN KEY (investment_portfolio_id) REFERENCES public.investment_portfolio(investment_portfolio_id);
                        
                        
            ALTER TABLE ONLY public.investment_portfolio_transaction
                ADD CONSTRAINT investor_fkey FOREIGN KEY (investor_id) REFERENCES public.investor(investor_id);
                        
                        
            ALTER TABLE ONLY public.investment_portfolio
                ADD CONSTRAINT investor_fkey FOREIGN KEY (investor_id) REFERENCES public.investor(investor_id) NOT VALID;
                        
                        
            ALTER TABLE ONLY public.position_transaction
                ADD CONSTRAINT position_fkey FOREIGN KEY (position_id) REFERENCES public."position"(position_id);
            """;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public.investor", Integer.class);
        } catch (Exception e) {
            jdbcTemplate.execute(createTables);
        }

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public.investor", Integer.class);

        if (count != null && count == 0) {
            jdbcTemplate.execute(insertData);
            jdbcTemplate.execute(finishCreate);
        }
    }
}
