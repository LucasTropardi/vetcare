package com.lucast.vetcare.fiscal.util.impressao;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class ImpressaoUtil {

    public static Impressao impressaoPadraoNFCe(String xml, String urlConsulta, BufferedImage logo) throws FiscalException {
        try {
            Impressao impressaoNFCe = new Impressao();
            impressaoNFCe.setXml(xml);
            impressaoNFCe.setPathExpression("/");
            impressaoNFCe.setJasper(JasperCompileManager.compileReport(ImpressaoUtil.class.getResourceAsStream( logo == null? "/jrxml/nfce/danfce.jrxml" : "/jrxml/nfce/danfce_logo.jrxml")));
            impressaoNFCe.getParametros().put("LOGO", logo);
            impressaoNFCe.getParametros().put("UrlConsulta", urlConsulta);
            return impressaoNFCe;
        } catch (JRException e) {
            e.printStackTrace();
            throw new FiscalException("Erro", "Erro ao imprimir a danfce <br>" + e.getMessage());
        }
    }

    public static String leArquivo(String caminhoArquivo) throws IOException {
        if (!Files.exists(Paths.get(verifica(caminhoArquivo)
                .orElseThrow(() -> new IllegalArgumentException("Arquivo não pode ser nulo/vazio."))))) {
            throw new FileNotFoundException("Arquivo " + caminhoArquivo + " não encontrado.");
        }
        List<String> list = Files.readAllLines(Paths.get(caminhoArquivo));
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        list.forEach(joiner::add);

        return joiner.toString();
    }

    public static <T> Optional<T> verifica(T obj) {
        if (obj == null) {
            return Optional.empty();
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty() ? Optional.empty() : Optional.of(obj);
        }

        final String s = String.valueOf(obj).trim();

        return s.isEmpty() || s.equalsIgnoreCase("null") ? Optional.empty() : Optional.of(obj);
    }

    public static JasperReport getJasper(String caminho) throws FiscalException {
        return ImpressaoUtil.carregaJasperResources(caminho);
    }

    public static JasperReport carregaJasperResources(String caminhoJasper) throws FiscalException {
        try (InputStream in = ImpressaoUtil.class.getResourceAsStream(caminhoJasper)) {
            if (in == null) {
                throw new FiscalException(String.format("Jasper não encontrado %s", caminhoJasper));
            }
            return (JasperReport) JRLoader.loadObject(in);
        } catch (IOException | JRException | FiscalException e) {
            throw new FiscalException(String.format("Erro ao carregar Jasper %s", caminhoJasper));
        }
    }
}
