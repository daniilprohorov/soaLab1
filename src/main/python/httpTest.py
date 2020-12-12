import requests

urlBase = 'http://localhost:8080/lab1coa-1.0-SNAPSHOT/'
urlProducts = urlBase + 'products'
urlOrganizations = urlBase + 'organizations'

product1data = {'name': 'Product1', 'x': 0, 'y': 0.5, 'price': 999, 'unitofmeasure':'pcs', 'manufacturer':1}
productCreateResponce = requests.post(urlProducts, data=product1data)
print('Product create')
print(productCreateResponce.content)


productDeleteResponce = requests.delete(urlProducts + '/4')
print('Product delete')
print(productDeleteResponce)
print(productDeleteResponce.content)

productPatchData = {'name': 'LOOOL234124', 'x': 0, 'y': 0.5, 'price': 999}
productPatchResponce = requests.put(urlProducts + '/9', data=productPatchData)
print('Product patch')
print(productPatchResponce)
print(productPatchResponce.content)

org1data = {'name': 'Org1', 'fullname': 'Organization one', 'employeescount':1000}
orgCreateResponce = requests.post(urlOrganizations, data=org1data)
print('Organization create')
print(orgCreateResponce.content)

orgDeleteResponce = requests.delete(urlOrganizations + '/2')
print('Organization delete')
print(orgDeleteResponce)
print(orgDeleteResponce.content)

orgPatchData = {'name': 'LOOOL234124', 'fullname': 'lolkek', 'employeescount': 999}
orgPatchResponce = requests.put(urlOrganizations + '/1', data=orgPatchData)
print('Product patch')
print(orgPatchResponce)
print(orgPatchResponce.content)




