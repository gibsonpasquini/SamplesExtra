package br.com.sensedia;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * * Cliente para consumir a API /orders
 */
public class Orders {

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

	public Orders(String url, String authToken, String appToken) {
		this.url = url;
		this.authToken = authToken;
		this.appToken = appToken;

	}

	/**
	 * Método utilizado para realizar a chamada ao WebService Restful que
	 * registra a entrega do pedido.
	 * 
	 * PUT /orders/{orderId}/status/delivered/
	 * 
	 * @param orderId
	 *            ID do pedido.
	 * @param occurenceDt
	 *            Nova data de entrega d o item.
	 * @param extraDescription
	 *            Texto com o motivo da alteração.
	 * @param originDeliveryID
	 *            Id da entrega para o lojista no parceiro.
	 * @return
	 */
	public String registerDelivery(String orderId, Date occurenceDt,
			String originDeliveryID, String extraDescription) {
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

			WebResource webResource = client.resource(url + "/orders/"
					+ orderId + "/status/delivered/");

			DateTime dt = new DateTime(occurenceDt);

			// Parâmetros da requisição
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("occurenceDt", dt.toString());
			data.put("originDeliveryId", originDeliveryID);
			data.put("extraDescription", extraDescription);

			String in = new ObjectMapper().writeValueAsString(data);

			response = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.put(ClientResponse.class, in);

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
	 * Método utilizado para realizar a chamada ao WebService Restful que
	 * recupera uma lista de pedidos com status aprovado.
	 * 
	 * GET /orders/status/approved/
	 * 
	 * @param offset
	 *            Parâmetro utilizado para limitar a quantidade de registros
	 *            trazidos por página.
	 * @param limit
	 *            Parâmetro utilizado para limitar a quantidade de registros
	 *            trazidos pela operação.
	 * @return Lista de pedidos com status aprovados.
	 */
	public String getApprovedOrders(String offset, String limit) {
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

			// Parametros da requisição
			MultivaluedMap<String, String> queryParameters = new MultivaluedMapImpl();
			queryParameters.add("_offset", offset);
			queryParameters.add("_limit", limit);

			WebResource webResource = client.resource(
					url + "/orders/status/approved").queryParams(
					queryParameters);

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

}
