package br.com.sensedia;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Cliente para consumir a API /products
 */
public class Products {

	/**
	 * URI completo do Serviço: domínio + contexto.<br/>
	 * Exemplo: http://api.extra.com.br/api/v1/sellerItems
	 */
	private String url;
	/**
	 * Token de autorização que identifica o Lojista que está invocando a API.
	 */
	private String authToken;
	/**
	 * Token de aplicação que identifica a Aplicação que está invocando a API.
	 */
	private String appToken;

	public Products(String url, String authToken, String appToken) {
		this.url = url;
		this.authToken = authToken;
		this.appToken = appToken;
	}

	/**
	 * Método utilizado para realizar a chamada ao WebService Restful que
	 * executal consulta de produtos.
	 * 
	 * @param offset
	 *            Parâmetro utilizado para limitar a quantidade de registros
	 *            trazidos por página.
	 * @param limit
	 *            Parâmetro utilizado para limitar a quantidade de registros
	 *            trazidos pela operação.
	 * @param searchText
	 *            Texto livre para busca de produtos.
	 * @param idCategory
	 *            ID da categoria utilizada para realizar busca de produtos.
	 * 
	 * @return Lista de produtos consultada.
	 */
	public String getProducts(String offset, String limit, String searchText,
			Integer idCategory) {

		ClientResponse response = null;

		try {

			Client client = Client.create();

			// Headers da requisição
			client.addFilter(new ClientFilter() {
				@Override
				public ClientResponse handle(final ClientRequest clientRequest)
						throws ClientHandlerException {

					final MultivaluedMap<String, Object> headers = clientRequest
							.getHeaders();
					headers.add("nova-auth-token", authToken);
					headers.add("nova-app-token", appToken);

					return getNext().handle(clientRequest);
				}
			});

			// Parâmetros da requisição
			MultivaluedMap<String, String> queryParameters = new MultivaluedMapImpl();
			queryParameters.add("_offset", offset);
			queryParameters.add("_limit", limit);
			// Um dos dois parâmetros precisa ser inserido na consulta:
			// searchText ou idCategory
			if (searchText != null && searchText.length() == 0) {
				queryParameters.add("searchText", searchText);
			} else if (idCategory != null) {
				queryParameters.add("idCategory", idCategory.toString());
			} else {
				throw new RuntimeException(
						"É obrigatório inserir pelo menos um dos parâmetros: searchText ou idCategory.");
			}

			WebResource webResource = client.resource(url + "/products")
					.queryParams(queryParameters);

			response = webResource.accept(MediaType.APPLICATION_JSON).get(
					ClientResponse.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

		} catch (Exception e) {
			// Fazer tratamento de erro adequado.
			return e.toString();

		}

		return response.getEntity(String.class);

	}

	/**
	 * Método utilizado para realizar a chamada ao WebService Restful que
	 * consulta um produto pelo seu código SKU.
	 * 
	 * GET /products/{skuId}
	 * 
	 * @param skuID
	 *            ID do produto
	 * @return Produto consultado
	 */
	public String getProductsBySkuID(String skuID) {

		ClientResponse response = null;

		try {

			Client client = Client.create();

			// Headers da requisição
			client.addFilter(new ClientFilter() {
				@Override
				public ClientResponse handle(final ClientRequest clientRequest)
						throws ClientHandlerException {

					final MultivaluedMap<String, Object> headers = clientRequest
							.getHeaders();
					headers.add("nova-auth-token", authToken);
					headers.add("nova-app-token", appToken);

					return getNext().handle(clientRequest);
				}
			});

			WebResource webResource = client.resource(url + "/products/"
					+ skuID);

			response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(
					ClientResponse.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

		} catch (Exception e) {
			// Fazer tratamento de erro adequado.
			return e.toString();
		}

		return response.getEntity(String.class);

	}
}
