with open('JenkinsFile-windows', 'r') as file:
        file_content = file.read()

# Esegui le sostituzioni necessarie
file_content = file_content.replace('\\\\', '/')
file_content = file_content.replace('bat', 'sh')
file_content = file_content.replace('start /b cmd /c "kubectl port-forward service/emofy-login-service 8085:8085"', 'nohup kubectl port-forward service/emofy-login-service 8085:8085 &')
file_content = file_content.replace('start /b cmd /c "kubectl port-forward service/emofy-image-storage 8081:8081"', 'nohup kubectl port-forward service/emofy-image-storage 8081:8081 &')

# Aggiungi altre sostituzioni necessarie
# file_content = file_content.replace('altro_carattere', 'sostituzione')
with open('JenkinsFile', 'w') as file:
    file.write(file_content)