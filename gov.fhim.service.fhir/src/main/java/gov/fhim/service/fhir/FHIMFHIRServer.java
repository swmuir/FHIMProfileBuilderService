package gov.fhim.service.fhir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mdht.uml.fhir.transform.ModelExporter;
import org.springframework.web.cors.CorsConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.narrative.INarrativeGenerator;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

/**
 * This servlet is the actual FHIR server itself
 */
public class FHIMFHIRServer extends RestfulServer {

	private static final long serialVersionUID = 1L;

	ModelExporter me = new ModelExporter();

	/**
	 * Constructor
	 */
	public FHIMFHIRServer() {
		super(FhirContext.forR4());
	}

	/**
	 * This method is called automatically when the
	 * servlet is initializing.
	 */
	@Override
	public void initialize() {

		this.getServletContext();

		this.setFhirContext(FhirContext.forR4());

		/*
		 * Two resource providers are defined. Each one handles a specific
		 * type of resource.
		 */
		List<IResourceProvider> providers = new ArrayList<IResourceProvider>();
		providers.add(new StructuredDefinitionProvider(getServletContext(), getFhirContext()));
		setResourceProviders(providers);

		/*
		 * Use a narrative generator. This is a completely optional step,
		 * but can be useful as it causes HAPI to generate narratives for
		 * resources which don't otherwise have one.
		 */
		INarrativeGenerator narrativeGen = new DefaultThymeleafNarrativeGenerator();
		getFhirContext().setNarrativeGenerator(narrativeGen);

		/*
		 * Enable CORS
		 */
		CorsConfiguration config = new CorsConfiguration();
		CorsInterceptor corsInterceptor = new CorsInterceptor(config);
		config.addAllowedHeader("Accept");
		config.addAllowedHeader("Content-Type");
		config.addAllowedOrigin("*");
		config.addExposedHeader("Location");
		config.addExposedHeader("Content-Location");
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		registerInterceptor(corsInterceptor);

		/*
		 * This server interceptor causes the server to return nicely
		 * formatter and coloured responses instead of plain JSON/XML if
		 * the request is coming from a browser window. It is optional,
		 * but can be nice for testing.
		 */
		registerInterceptor(new ResponseHighlighterInterceptor());

		/*
		 * Tells the server to return pretty-printed responses by default
		 */
		setDefaultPrettyPrint(true);

		this.setDefaultResponseEncoding(EncodingEnum.JSON);

	}

}
