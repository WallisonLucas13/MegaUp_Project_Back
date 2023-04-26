package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.dtos.OrcamentoAdressTo;
import com.example.MegaUp_Server.models.Etapa;
import com.example.MegaUp_Server.models.Material;
import com.example.MegaUp_Server.models.Servico;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class CreateAttachmentFile {

    public String create(Servico servico,
                         OrcamentoAdressTo orcamentoAdressTo,
                         String mailCompany,
                         String empresa,
                         String empresaSubTitle,
                         String cnpj,
                         String telefone) throws DocumentException, IOException {

        //REGRAS DE NEGOCIO
        List<Material> materiais = servico.getMateriais();

        boolean ocultarMateriais = orcamentoAdressTo.isOcultarMateriais();
        boolean ocultarMaoDeObra = orcamentoAdressTo.isOcultarMaoDeObra();
        boolean ocultarDesconto = orcamentoAdressTo.isOcultarDesconto();
        //---------------------------------------------------------------------

        //INSTANCIA DO DOCUMENTO
        Document document = new Document();

        String documentName = "Orcamento.pdf";

        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("file:files\\" + documentName);
        PdfWriter.getInstance(document, new FileOutputStream(resource.getFile()));

        Rectangle rectangle = new Rectangle(PageSize.A4);

        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorder(Rectangle.BOX);
        rectangle.setBorderWidth(3);
        rectangle.setBorderColor(BaseColor.BLACK);

        document.setPageSize(rectangle);
        document.open();
        document.add(rectangle);
        //--------------------------------------------------------------------------------------------

        //FONTS
        Font fontValues = new Font();
        fontValues.setColor(new BaseColor(0, 128, 0));
        fontValues.setSize(14);

        Font fontImportant = new Font();
        fontImportant.setColor(new BaseColor(174, 6, 3));
        fontImportant.setSize(14);

        Font fontTitle = new Font();
        fontTitle.setStyle(Font.BOLD);
        fontTitle.setSize(25);
        fontTitle.setColor(new BaseColor(255, 187, 51));

        Font fontEndQuestions = new Font();
        fontEndQuestions.setColor(BaseColor.BLACK);
        fontEndQuestions.setSize(12);

        Font fontEndQuestionsSubtitle = new Font();
        fontEndQuestionsSubtitle.setColor(new BaseColor(123, 123, 123, 1));
        fontEndQuestionsSubtitle.setSize(13);
        fontEndQuestionsSubtitle.setStyle(Font.BOLD);

        Font fontEndQuestionsStyled = new Font();
        fontEndQuestionsStyled.setColor(BaseColor.BLACK);
        fontEndQuestionsStyled.setSize(15);
        fontEndQuestionsStyled.setFamily(FontFactory.TIMES_ITALIC);
        fontEndQuestionsStyled.setStyle(Font.BOLDITALIC);

        //TITLE
        Paragraph title = new Paragraph(new Phrase(20f,empresa, FontFactory.getFont(FontFactory.COURIER_BOLD, 18F, BaseColor.BLACK)));
        title.setAlignment(Element.ALIGN_CENTER);
        Font fontDeLink = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, BaseColor.BLACK);
        Paragraph subtitle = new Paragraph(new Phrase(14F, empresaSubTitle, fontDeLink));
        subtitle.setAlignment(Element.ALIGN_CENTER);

        document.addTitle("Orçamento");

        document.add(title);
        document.add(subtitle);
        //-------------------------------------------------------------------------------------------------

        document.add(divider());

        //PRESTADOR HEADER
        Phrase headerPrestador = new Phrase("Prestador", fontEndQuestionsStyled);

        Paragraph prestador = new Paragraph(headerPrestador);
        prestador.setSpacingBefore(8f);
        prestador.setSpacingAfter(10f);
        prestador.setAlignment(Element.ALIGN_LEFT);
        document.add(prestador);
        //----------------------------------------------------------------------------------------------

        //EMPRESA
        Phrase headerEmpresa = new Phrase("Empresa: ", fontEndQuestionsSubtitle);
        Phrase bodyEmpresa = new Phrase(empresa, fontEndQuestions);
        headerEmpresa.add(bodyEmpresa);

        Paragraph empresaP = new Paragraph(headerEmpresa);
        empresaP.setSpacingAfter(5f);
        empresaP.setAlignment(Element.ALIGN_LEFT);
        document.add(empresaP);
        //----------------------------------------------------------------------------------------------

        //CNPJ
        Phrase headerCNPJ = new Phrase("Cnpj: ", fontEndQuestionsSubtitle);
        Phrase bodyCNPJ = new Phrase(cnpj, fontEndQuestions);
        headerCNPJ.add(bodyCNPJ);

        Paragraph cnpjP = new Paragraph(headerCNPJ);
        cnpjP.setSpacingAfter(5f);
        cnpjP.setAlignment(Element.ALIGN_LEFT);
        document.add(cnpjP);
        //----------------------------------------------------------------------------------------------

        //EMAIL
        Phrase headerEmail = new Phrase("Email: ", fontEndQuestionsSubtitle);
        Phrase bodyEmail = new Phrase(mailCompany, fontEndQuestions);
        headerEmail.add(bodyEmail);

        Paragraph email = new Paragraph(headerEmail);
        email.setSpacingAfter(5f);
        email.setAlignment(Element.ALIGN_LEFT);
        document.add(email);
        //----------------------------------------------------------------------------------------------

        //TELEFONE
        Phrase headerTel = new Phrase("Telefone: ", fontEndQuestionsSubtitle);
        Phrase bodyTel = new Phrase(telefone, fontEndQuestions);
        headerTel.add(bodyTel);

        Paragraph tel = new Paragraph(headerTel);
        tel.setAlignment(Element.ALIGN_LEFT);
        document.add(tel);
        //----------------------------------------------------------------------------------------------

        //LOCAL
        Phrase headerLocal = new Phrase("Localização: ", fontEndQuestionsSubtitle);
        Phrase bodyLocal = new Phrase("Uberlândia - MG", fontEndQuestions);
        headerLocal.add(bodyLocal);

        Paragraph local = new Paragraph(headerLocal);
        local.setAlignment(Element.ALIGN_LEFT);
        document.add(local);
        //----------------------------------------------------------------------------------------------

        //SERVICO HEADER
        Phrase headerServico = new Phrase("Serviço", fontEndQuestionsStyled);

        Paragraph servicoP = new Paragraph(headerServico);
        servicoP.setSpacingBefore(10f);
        servicoP.setSpacingAfter(10f);
        servicoP.setAlignment(Element.ALIGN_LEFT);
        document.add(servicoP);
        //----------------------------------------------------------------------------------------------

        //NOME
        Phrase headerNome = new Phrase("Nome: ", fontEndQuestionsSubtitle);
        Phrase bodyNome = new Phrase(servico.getNome(), fontEndQuestions);
        headerNome.add(bodyNome);

        Paragraph nome = new Paragraph(headerNome);
        nome.setSpacingAfter(5f);
        nome.setAlignment(Element.ALIGN_LEFT);
        document.add(nome);
        //----------------------------------------------------------------------------------------------

        //DESCRICAO
        Phrase headerDesc = new Phrase("Descrição: ", fontEndQuestionsSubtitle);
        Phrase bodyDesc = new Phrase(servico.getDesc(), fontEndQuestions);
        headerDesc.add(bodyDesc);

        Paragraph desc = new Paragraph(headerDesc);
        desc.setAlignment(Element.ALIGN_LEFT);
        document.add(desc);
        //----------------------------------------------------------------------------------------------

        //MATERIAIS HEADER
        if(!ocultarMateriais) {
            Phrase headerMateriais = new Phrase("Materiais", fontEndQuestionsStyled);

            Paragraph materiaisP = new Paragraph(headerMateriais);
            materiaisP.setSpacingBefore(-18f);
            materiaisP.setSpacingAfter(10f);
            materiaisP.setAlignment(Element.ALIGN_RIGHT);
            document.add(materiaisP);
            //----------------------------------------------------------------------------------------------

            //MATERIAL
            if (materiais.isEmpty()) {
                Phrase bodyEmpty = new Phrase("Não há nenhum material", fontEndQuestionsSubtitle);

                Paragraph e = new Paragraph(bodyEmpty);
                e.setSpacingAfter(5f);
                e.setAlignment(Element.ALIGN_RIGHT);
                document.add(e);
            }
            materiais.stream().forEach((material) -> {

                Phrase bodyMaterial = formatarMaterial(material, fontValues, fontImportant, fontEndQuestions);

                Paragraph m = new Paragraph(bodyMaterial);
                m.setSpacingAfter(5f);
                m.setAlignment(Element.ALIGN_RIGHT);
                try {
                    document.add(m);
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                }

            });
            //----------------------------------------------------------------------------------------------

            //MATERIAL TOTAL
            if (!materiais.isEmpty()) {
                Phrase headerTotalM = new Phrase("Total: ", fontEndQuestionsSubtitle);
                Phrase bodyTotalM = new Phrase("R$ " + servico.getValorTotalMateriais() + ",00", fontValues);
                headerTotalM.add(bodyTotalM);

                Paragraph totalM = new Paragraph(headerTotalM);
                totalM.setAlignment(Element.ALIGN_RIGHT);
                document.add(totalM);
            }
        }
        //----------------------------------------------------------------------------------------------

        //MAO DE OBRA
        if(!ocultarMaoDeObra) {
            Phrase headerObra = new Phrase("Mão de Obra: ", fontEndQuestionsStyled);
            Phrase bodyObra = new Phrase("R$ " + servico.getMaoDeObra() + ",00", fontValues);
            headerObra.add(bodyObra);

            Paragraph obra = new Paragraph(headerObra);
            obra.setSpacingBefore(10f);
            obra.setAlignment(Element.ALIGN_LEFT);
            document.add(obra);
        }
        //----------------------------------------------------------------------------------------------

        document.add(divider());

        //SubTotal
        Phrase headerSub = new Phrase("SubTotal: ", fontEndQuestionsStyled);
        int t = servico.getMaoDeObra() + servico.getValorTotalMateriais();
        Phrase bodySub = new Phrase("R$ " + t + ",00\n_____________________", fontValues);
        headerSub.add(bodySub);

        Paragraph sub = new Paragraph(headerSub);
        sub.setSpacingBefore(10f);
        sub.setAlignment(Element.ALIGN_CENTER);
        document.add(sub);
        //----------------------------------------------------------------------------------------------

        //ETAPAS HEADER
        Phrase headerEtapas = new Phrase("Etapas", fontEndQuestionsStyled);

        Paragraph etapasP = new Paragraph(headerEtapas);
        etapasP.setSpacingBefore(-25f);
        etapasP.setSpacingAfter(10f);
        etapasP.setAlignment(Element.ALIGN_RIGHT);
        document.add(etapasP);
        //----------------------------------------------------------------------------------------------

        //ETAPAS
        List<Etapa> etapas = servico.getEtapas();
        etapas.sort(Comparator.comparingLong(Etapa::getIden));
        etapas.stream().forEach((etapa) -> {

            Phrase bodyEtapa = new Phrase(etapa.getIden() + "° Etapa -> R$ " + etapa.getValor() + ",00");

            Paragraph e = new Paragraph(bodyEtapa);
            e.setSpacingAfter(5f);
            e.setAlignment(Element.ALIGN_RIGHT);
            try {
                document.add(e);
            } catch (DocumentException a) {
                throw new RuntimeException(a);
            }

        });
        //-----------------------------------------------------------------------------------------------


        //Desconto
        if(!ocultarDesconto) {
            Phrase headerDesconto = new Phrase("Desconto: ", fontEndQuestionsStyled);
            Phrase bodyDesconto = new Phrase(servico.getDesconto() + "% | " + "R$ " + (Integer.parseInt(servico.getValorPagamentoFinal()) * servico.getDesconto()) / 100 + ",00", fontImportant);
            headerDesconto.add(bodyDesconto);

            Paragraph desconto = new Paragraph(headerDesconto);
            desconto.setSpacingBefore(10f);
            desconto.setAlignment(Element.ALIGN_LEFT);
            document.add(desconto);
        }
        //----------------------------------------------------------------------------------------------

        //ENTRADA
        Phrase headerEntrada = new Phrase("Entrada: ", fontEndQuestionsStyled);
        Phrase bodyEntrada = new Phrase(servico.getPorcentagemEntrada() + "% | " + "R$ " + servico.getValorEntrada() + ",00", fontValues);
        headerEntrada.add(bodyEntrada);

        Paragraph entrada = new Paragraph(headerEntrada);
        entrada.setSpacingBefore(10f);
        entrada.setAlignment(Element.ALIGN_LEFT);
        document.add(entrada);
        //----------------------------------------------------------------------------------------------

        document.add(divider());

        //Total a pagar
        Phrase value = new Phrase("R$ " + servico.getValorPagamentoFinal() + ",00", fontValues);
        Phrase text = new Phrase("Total a pagar na conclusão do serviço: ", fontEndQuestionsStyled);
        text.add(value);

        Paragraph pagamentoFinal = new Paragraph(text);
        pagamentoFinal.setSpacingBefore(10f);
        pagamentoFinal.setSpacingAfter(15f);
        pagamentoFinal.setAlignment(Element.ALIGN_LEFT);
        document.add(pagamentoFinal);
        //-----------------------------------------------------------------------------------------------

        //Formas Pagamento
        Phrase headerPagamentoForm = new Phrase("Formas de Pagamento: ", fontEndQuestionsSubtitle);
        Phrase bodyPagamentoForm = new Phrase("DÉBITO | CRÉDITO | PIX | DINHEIRO", FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
        headerPagamentoForm.add(bodyPagamentoForm);

        Paragraph formaPagamentoFinal = new Paragraph(headerPagamentoForm);

        formaPagamentoFinal.setAlignment(Element.ALIGN_LEFT);
        formaPagamentoFinal.setSpacingAfter(10f);
        document.add(formaPagamentoFinal);
        //------------------------------------------------------------------------------------------------

        document.add(divider());

        //Data Emissão
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Paragraph time = new Paragraph(new Phrase(12F,"Data de Emissão: " + format.format(date), FontFactory.getFont(FontFactory.HELVETICA, 11, new BaseColor(123, 123, 123, 1))));
        time.setAlignment(Element.ALIGN_RIGHT);
        time.setSpacingBefore(9f);
        document.add(time);
        //------------------------------------------------------------------------------------------------

        //Mensagem Biblica
        Paragraph msg = new Paragraph(new Phrase(14F, "O Senhor é o meu pastor: nada me faltará.\nSalmos 23:1", FontFactory.getFont(FontFactory.TIMES_ITALIC, 11, new BaseColor(123, 123, 123, 1))));
        msg.setAlignment(Element.ALIGN_LEFT);
        msg.setSpacingBefore(-14f);
        document.add(msg);
        //------------------------------------------------------------------------------------------------

        //Footer
        Paragraph footer = new Paragraph(new Phrase("By "+ empresa, FontFactory.getFont(FontFactory.COURIER_BOLD, 11, BaseColor.BLACK)));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(25f);
        document.add(footer);
        //-------------------------------------------------------------------------------------------------

        document.close();

        return documentName;
    }

    //FORMATAR MATERIAL STRING
    private Phrase formatarMaterial(Material material, Font valuesFont, Font importantFont, Font fontPadrao){

        Phrase quant = new Phrase(String.valueOf(material.getQuant()), importantFont);
        Phrase unitario = new Phrase("R$ " + material.getValor() + ",00 | ", valuesFont);
        unitario.add(quant);

        int mult = material.getValor()* material.getQuant();
        Phrase res = new Phrase(" = R$ " + mult + ",00", valuesFont);

        Phrase start = new Phrase(material.getNome() + " | ", fontPadrao);
        start.add(unitario);
        start.add(res);
        return start;
    }
    //------------------------------------------------------------------------------------------------------

    //DIVISER DESIGN
    private Paragraph divider(){
        Phrase p = new Phrase("__________________________________________________________________", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(123, 123, 123, 1)));
        Paragraph a = new Paragraph(p);
        a.setAlignment(Element.ALIGN_CENTER);
        return new Paragraph(p);
    }
    //-------------------------------------------------------------------------------
}
