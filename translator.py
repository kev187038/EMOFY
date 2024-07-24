import re

# Leggi il contenuto del file JenkinsFile-windows
with open('JenkinsFile-windows', 'r') as file:
    file_content = file.read()

# Esegui le sostituzioni necessarie
file_content = file_content.replace('\\\\', '/')
file_content = file_content.replace('bat', 'sh')

# Usa una regex per trovare e sostituire tutti i comandi di port-forward
file_content = re.sub(
    r'start /b cmd /c "kubectl port-forward service/([^ ]+) (\d+):(\d+)"',
    r'nohup kubectl port-forward service/\1 \2:\3 &',
    file_content
)

# Aggiungi altre sostituzioni necessarie
# file_content = file_content.replace('altro_carattere', 'sostituzione')

# Scrivi il contenuto modificato nel nuovo file JenkinsFile
with open('JenkinsFile', 'w') as file:
    file.write(file_content)
