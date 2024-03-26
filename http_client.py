#################################################
# Simple Http Client
#@Author Giuseppe Valente <valentepeppe@gmail.com>
##################################################
import requests

def main():
    url = 'http://localhost:8081/api/user/insert'
    headers = {
            'content-type': 'application/json',
            'Authorization' : 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1NZU1RFTV9BRE1JTklTVFJBVE9SIiwiUk9MRV9TVVBFUkFETUlOIl0sImVuYWJsZWQiOnRydWUsInVzZXJuYW1lIjoic3VwZXJhZG1pbiIsInN1YiI6InVzZXIiLCJpYXQiOjE3MTE0NzkzNzksImV4cCI6MTcxMTQ4Mjk3OX0.v44GBDzwHSLip6CSKz7UzWU3dryyZjm2CJjpK1gg1MY'
            }
    params = {"name":"Giuseppe","surname":"Valente","username":"valentepeppe","email":"valentepeppe@gmail.com","password":"vlnGPP87$","roles":"","enabled":True}
    #r = requests.post(url, params=params, headers=headers)
    data = {
        
    }
    r = requests.post(url, json=params, headers=headers)
    print(r.text)



if __name__ == "__main__":
    main()