package fxreload.model;

import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

public class ModelTest {

	@Test
	public void test() throws Exception {
		FxReload reload = new FxReload();
		reload.getFile().addAll(Arrays.asList("a", "b"));
		WebWatch p = new WebWatch();
		p.setUrl("aaaa");
		p.setFile(Arrays.asList("a", "b"));
		reload.getWebPage().add(p);

		JAXBContext jaxbContext = JAXBContext.newInstance(FxReload.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		// jaxbMarshaller.marshal(customer, file);
		jaxbMarshaller.marshal(reload, System.out);
	}

}
