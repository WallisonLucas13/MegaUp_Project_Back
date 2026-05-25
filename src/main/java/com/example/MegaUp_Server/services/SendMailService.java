package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.dtos.OrcamentoAdressTo;
import com.example.MegaUp_Server.models.Servico;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Log4j2
public class SendMailService {

    @Value("${MAIL_ADRESS_KEY}")
    private String mailAdressKey;

    @Value("${MAIL_COMPANY}")
    private String mailCompany;

    @Value("${EMPRESA}")
    private String empresa;

    @Value("${CNPJ}")
    private String cnpj;

    @Value("${TELEFONE}")
    private String telefone;

    @Value("${EMPRESA_SUBTITLE}")
    private String empresaSubTitle;

    @Value("${FILES_STORAGE_PATH:files/}")
    private String filesStoragePath;

    private final JavaMailSender javaMailSender;
    private final MailBody mailBody;
    private final CreateAttachmentFile createAttachmentFile;
    private final CodeKeyGenerator codeKeyGenerator;

    public String createMailAndSend() {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(mailAdressKey);
        mail.setSubject("Código de Acesso");
        String code = codeKeyGenerator.gerarKey();
        mail.setText(mailBody.bodyKeyMail(code));
        javaMailSender.send(mail);
        return code;
    }

    public void createMailAndSendWithAttachments(OrcamentoAdressTo adress, Servico servico) {

        String documentName = null;
        try {
            documentName = createAttachmentFile.create(servico,
                    adress, mailCompany, empresa, empresaSubTitle, cnpj, telefone);

            Path filePath = Paths.get(filesStoragePath, documentName);
            FileSystemResource fileResource = new FileSystemResource(filePath.toFile());

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setSubject("Orçamento");
            helper.setFrom(mailCompany);
            helper.setTo(adress.adress());
            helper.addAttachment(fileResource.getFilename(), fileResource);
            helper.setText(mailBody.attachmentBody(empresa));
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error("Falha ao enviar orçamento por e-mail: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao enviar orçamento. Tente novamente.");
        } finally {
            if (documentName != null) {
                try {
                    Files.deleteIfExists(Paths.get(filesStoragePath, documentName));
                } catch (Exception e) {
                    log.warn("Não foi possível remover arquivo temporário: {}", documentName);
                }
            }
        }
    }
}
