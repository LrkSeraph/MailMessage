# MailMessage
## Description
it can send and receive email <br>
when receive email, it will call a script with mail message as arguments, like: <code> script sender subject content</code> <br>
it use smtp with SSL to send email and imap with SSL to receive email <br>
## Usage
### Send EMail
```bash
MailMessae mailto [--no_log] [--config=file path] [--account=] [--password=] [--smtp_server=] [--smtp_port=] [--sender=] --recipient= --subject= message
```
### Receive EMail
```bash
MailMessae daemon [--no_log] [--pop3|--imap] [--config=file path] [--account=] [--password=] [--imap_server=] [--imap_port=] [--pop3_server=] [--pop3_port=] --script=
```
