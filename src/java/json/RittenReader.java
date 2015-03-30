package json;

import domain.Rit;
import domain.User;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

/*
 * Deze klasse is een JAX-RS provider die JSON kan parsen tot een User.
 */

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class RittenReader implements MessageBodyReader<Rit>
{
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return User.class.isAssignableFrom(type);
    }

    @Override
    public Rit readFrom(Class<Rit> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
    {
        try (JsonReader in = Json.createReader(entityStream)) {
            
            JsonObject jsonRit = in.readObject();
            Rit rit = new Rit();
            
            
            rit.setTitle(jsonRit.getString("title", null));
            rit.setAfstand(jsonRit.getInt("afstand", 0));
            
            return rit;
            
        } catch (JsonException | ClassCastException ex) {
            throw new BadRequestException("Ongeldige JSON invoer");
        }
    }
}
