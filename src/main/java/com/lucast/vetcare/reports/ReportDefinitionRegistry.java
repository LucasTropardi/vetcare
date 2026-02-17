package com.lucast.vetcare.reports;

import com.lucast.vetcare.reports.dto.ReportColumnResponse;
import com.lucast.vetcare.reports.dto.ReportDefinitionResponse;
import com.lucast.vetcare.reports.dto.ReportFilterResponse;
import com.lucast.vetcare.reports.dto.ReportOptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportDefinitionRegistry {

    private final Map<String, ReportDefinitionResponse> definitions;

    public ReportDefinitionRegistry() {
        Map<String, ReportDefinitionResponse> map = new LinkedHashMap<>();

        map.put("users", new ReportDefinitionResponse(
                "users",
                "Usuarios",
                "Relatorio de usuarios do sistema",
                List.of(
                        new ReportFilterResponse("query", "Busca", ReportFilterType.TEXT, null, "Nome ou e-mail"),
                        new ReportFilterResponse("active", "Ativo", ReportFilterType.BOOLEAN, null, null),
                        new ReportFilterResponse("role", "Perfil", ReportFilterType.SELECT, List.of(
                                new ReportOptionResponse("ADMIN", "ADMIN"),
                                new ReportOptionResponse("VET", "VET"),
                                new ReportOptionResponse("RECEPTION", "RECEPTION")
                        ), null)
                ),
                List.of(
                        new ReportColumnResponse("id", "ID"),
                        new ReportColumnResponse("name", "Nome"),
                        new ReportColumnResponse("email", "E-mail"),
                        new ReportColumnResponse("role", "Perfil"),
                        new ReportColumnResponse("active", "Ativo")
                ),
                List.of(ReportFormat.PDF, ReportFormat.CSV, ReportFormat.XLSX)
        ));

        map.put("tutors", new ReportDefinitionResponse(
                "tutors",
                "Tutores",
                "Relatorio cadastral de tutores",
                List.of(
                        new ReportFilterResponse("query", "Busca", ReportFilterType.TEXT, null, "Nome, documento, telefone ou e-mail"),
                        new ReportFilterResponse("active", "Ativo", ReportFilterType.BOOLEAN, null, null),
                        new ReportFilterResponse("hasCompany", "Tem empresa", ReportFilterType.BOOLEAN, null, null),
                        new ReportFilterResponse("hasPet", "Tem pet", ReportFilterType.BOOLEAN, null, null),
                        new ReportFilterResponse("hasContact", "Tem contato", ReportFilterType.BOOLEAN, null, null)
                ),
                List.of(
                        new ReportColumnResponse("id", "ID"),
                        new ReportColumnResponse("name", "Nome"),
                        new ReportColumnResponse("document", "Documento"),
                        new ReportColumnResponse("phone", "Telefone"),
                        new ReportColumnResponse("email", "E-mail"),
                        new ReportColumnResponse("active", "Ativo")
                ),
                List.of(ReportFormat.PDF, ReportFormat.CSV, ReportFormat.XLSX)
        ));

        map.put("pets", new ReportDefinitionResponse(
                "pets",
                "Pets",
                "Relatorio cadastral de pets",
                List.of(
                        new ReportFilterResponse("query", "Busca", ReportFilterType.TEXT, null, "Nome do pet, tutor ou especie"),
                        new ReportFilterResponse("tutorId", "ID tutor", ReportFilterType.NUMBER, null, null),
                        new ReportFilterResponse("active", "Ativo", ReportFilterType.BOOLEAN, null, null),
                        new ReportFilterResponse("species", "Especie", ReportFilterType.SELECT, List.of(
                                new ReportOptionResponse("DOG", "DOG"),
                                new ReportOptionResponse("CAT", "CAT"),
                                new ReportOptionResponse("BIRD", "BIRD"),
                                new ReportOptionResponse("RABBIT", "RABBIT"),
                                new ReportOptionResponse("HAMSTER", "HAMSTER"),
                                new ReportOptionResponse("GUINEA_PIG", "GUINEA_PIG"),
                                new ReportOptionResponse("FERRET", "FERRET"),
                                new ReportOptionResponse("REPTILE", "REPTILE"),
                                new ReportOptionResponse("SNAKE", "SNAKE"),
                                new ReportOptionResponse("LIZARD", "LIZARD"),
                                new ReportOptionResponse("TURTLE", "TURTLE"),
                                new ReportOptionResponse("FISH", "FISH"),
                                new ReportOptionResponse("HORSE", "HORSE"),
                                new ReportOptionResponse("COW", "COW"),
                                new ReportOptionResponse("PIG", "PIG"),
                                new ReportOptionResponse("OTHER", "OTHER")
                        ), null),
                        new ReportFilterResponse("othersSpecies", "Somente outras especies", ReportFilterType.BOOLEAN, null, null)
                ),
                List.of(
                        new ReportColumnResponse("id", "ID"),
                        new ReportColumnResponse("tutorId", "ID tutor"),
                        new ReportColumnResponse("tutorName", "Tutor"),
                        new ReportColumnResponse("name", "Nome"),
                        new ReportColumnResponse("species", "Especie"),
                        new ReportColumnResponse("active", "Ativo")
                ),
                List.of(ReportFormat.PDF, ReportFormat.CSV, ReportFormat.XLSX)
        ));

        map.put("products", new ReportDefinitionResponse(
                "products",
                "Produtos",
                "Relatorio cadastral de produtos",
                List.of(
                        new ReportFilterResponse("name", "Busca", ReportFilterType.TEXT, null, "Nome ou SKU"),
                        new ReportFilterResponse("category", "Categoria", ReportFilterType.SELECT, List.of(
                                new ReportOptionResponse("MEDICINE", "MEDICINE"),
                                new ReportOptionResponse("SUPPLY", "SUPPLY"),
                                new ReportOptionResponse("FEED", "FEED"),
                                new ReportOptionResponse("OTHER", "OTHER")
                        ), null),
                        new ReportFilterResponse("active", "Ativo", ReportFilterType.BOOLEAN, null, null)
                ),
                List.of(
                        new ReportColumnResponse("id", "ID"),
                        new ReportColumnResponse("sku", "SKU"),
                        new ReportColumnResponse("name", "Nome"),
                        new ReportColumnResponse("category", "Categoria"),
                        new ReportColumnResponse("unit", "Unidade"),
                        new ReportColumnResponse("salePrice", "Preco venda"),
                        new ReportColumnResponse("costPrice", "Preco custo"),
                        new ReportColumnResponse("minStock", "Estoque minimo"),
                        new ReportColumnResponse("active", "Ativo")
                ),
                List.of(ReportFormat.PDF, ReportFormat.CSV, ReportFormat.XLSX)
        ));

        map.put("customer-companies", new ReportDefinitionResponse(
                "customer-companies",
                "Empresas clientes",
                "Relatorio cadastral de empresas clientes",
                List.of(
                        new ReportFilterResponse("query", "Busca", ReportFilterType.TEXT, null, "Razao social, fantasia ou CNPJ"),
                        new ReportFilterResponse("tutorId", "ID tutor", ReportFilterType.NUMBER, null, null),
                        new ReportFilterResponse("active", "Ativo", ReportFilterType.BOOLEAN, null, null),
                        new ReportFilterResponse("hasAddress", "Tem endereco", ReportFilterType.BOOLEAN, null, null),
                        new ReportFilterResponse("hasFiscal", "Tem fiscal", ReportFilterType.BOOLEAN, null, null),
                        new ReportFilterResponse("hasContact", "Tem contato", ReportFilterType.BOOLEAN, null, null)
                ),
                List.of(
                        new ReportColumnResponse("id", "ID"),
                        new ReportColumnResponse("tutorId", "ID tutor"),
                        new ReportColumnResponse("legalName", "Razao social"),
                        new ReportColumnResponse("tradeName", "Nome fantasia"),
                        new ReportColumnResponse("cnpj", "CNPJ"),
                        new ReportColumnResponse("active", "Ativo")
                ),
                List.of(ReportFormat.PDF, ReportFormat.CSV, ReportFormat.XLSX)
        ));

        this.definitions = Map.copyOf(map);
    }

    public List<ReportDefinitionResponse> list() {
        return definitions.values().stream().toList();
    }

    public ReportDefinitionResponse getByKey(String reportKey) {
        ReportDefinitionResponse definition = definitions.get(reportKey);
        if (definition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report definition not found");
        }
        return definition;
    }
}
