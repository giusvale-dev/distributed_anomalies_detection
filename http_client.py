#################################################
# Simple Http Client
#@Author Giuseppe Valente <valentepeppe@gmail.com>
##################################################
import requests

def main():
    url = 'http://localhost:8081/api/user/insert'
    headers = {'content-type': 'application/json'}
    params = {'username': 'superadmin', 'password': 'HelloWolrd!123'}
    #r = requests.post(url, params=params, headers=headers)
    data = {
        
    }
    r = requests.post(url, data=, headers=headers)
    print(r.text)



if __name__ == "__main__":
    main()