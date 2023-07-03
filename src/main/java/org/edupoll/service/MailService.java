package org.edupoll.service;

public class MailService {

	/*
	private JavaMailSender mailSender;

	public void sendTestSimpleMail(MailTestRequest dto) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom("codingtest1023@gmail.com");
		message.setTo(dto.getEmailAddress());
		message.setSubject("가입인증메일");
		message.setText("메일테스트중입니다. \n 확인하세요.");
		mailSender.send(message);
	}
	
	public void snedTestHtmlMail(MailTestRequest dto) throws MessagingException {
		
		String uuid = UUID.randomUUID().toString();
		Random random= new Random();
		int ranNum = random.nextInt(1_000_000);
		String code = String.format("%06d", ranNum);
		
		String htmltxt = """
							<div>
								<h1>메일 테스트중</h1>
									<p style="color:red">
										HTML 메세지도 <b>전송</b>가능하다.
									</p>
									<p>
										쿠폰번호 : <i>%code%</i>
									</p>	
							
										<p>
											<a href="http://www.google.com">인증페이지로 이동</a>
										</p>
							
							</div>
		
						""".replace("%code%", code);
		
		MimeMessage message =mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(dto.getEmailAddress());
		helper.setFrom("codingtest1023@gmail.com");
		helper.setSubject("메일테스트2");
		helper.setText(htmltxt, true);
		
		mailSender.send(message);
	}
	*/
}
