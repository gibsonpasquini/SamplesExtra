package br.com.sensedia;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Cliente para consumir a API /sellerItems
 */
public class SellerItems {

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

	public SellerItems(String url, String authToken, String appToken) {
		this.url = url;
		this.authToken = authToken;
		this.appToken = appToken;
	}

	/**
	 * Método utilizado para realizar a chamada ao WebService Restful que traz a
	 * lista de produtos que são vendidos pelo lojista.
	 * 
	 * GET /sellerItems
	 * 
	 * @param offset
	 *            Parâmetro utilizado para limitar a quantidade de registros
	 *            trazidos por página.
	 * @param limit
	 *            Parâmetro utilizado para limitar a quantidade de registros
	 *            trazidos pela operação.
	 * @return Lista de produtos vendidos pelo lojista
	 */
	public String getSellerItems(String offset, String limit) {
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

			WebResource webResource = client.resource(url + "/sellerItems")
					.queryParams(queryParameters);

			response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(
					ClientResponse.class);

			if (response.getStatus() != 200) {
				// Fazer tratamento de erro adequado.
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
	 * Método utilizado para realizar a chamada ao WebService Restful que faz a
	 * associação do produto ao lojista.
	 * 
	 * POST /sellerItems
	 * 
	 * @param bodyParams
	 *            Mapa contendo os parâmetros que precisam ser passados no body
	 *            da requisição. Exemplo de conteúdo do mapa:
	 * 
	 *            { "skuOrigin": "string", "skuId": "string", "defaultPrice":
	 *            "500.00", "salePrice": "460.00", "availableQuantity": "100",
	 *            "installmentId": "20p3x", "totalQuantity": "250",
	 *            "crossDockingTime": 1 }
	 * 
	 * @return Retorno da requisição, composto do status e o location da
	 *         associação do produto ao lojista.
	 */
	public String postSellerItems(Map<String, Object> bodyParams) {
		ClientResponse response = null;

		try {

			Client client = Client.create();

			// Inclusão dos headers de requisição
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

			WebResource webResource = client.resource(url + "/sellerItems");

			String in = new ObjectMapper().writeValueAsString(bodyParams);

			response = webResource.type(MediaType.APPLICATION_JSON_TYPE).post(
					ClientResponse.class, in);

			if (response.getStatus() != 201) {
				// Fazer tratamento de erro adequado.
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
	 * atualiza a quantidade disponível para venda de um item do Lojista.
	 * 
	 * PUT /sellerItems/{skuId}/stock
	 * 
	 * @param skuId
	 *            ID do produto a venda
	 * @param availableQuantity
	 *            Quantidade disponível
	 * @param totalQuantity
	 *            Quantidade total de produtos
	 * @return Status da operação.
	 */
	public String putSellerItems(String skuId, String availableQuantity,
			String totalQuantity) {
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

			WebResource webResource = client.resource(url + "/sellerItems/"
					+ skuId + "/stock");

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("availableQuantity", availableQuantity);
			data.put("totalQuantity", totalQuantity);

			String in = new ObjectMapper().writeValueAsString(data);

			response = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.put(ClientResponse.class, in);

			if (response.getStatus() != 204) {
				// Fazer tratamento de erro adequado.
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

		} catch (Exception e) {
			// Fazer tratamento de erro adequado.
			return e.toString();
		}

		// Como as operações PUT possuem retorno 204 - NO CONTENT, o retorno é
		// composto através do status + a mensagem do status.
		String out = response.getStatus() + " - "
				+ response.getClientResponseStatus().name();
		return out;
	}
}
