
package resources;

import domain.Rit;
import domain.User;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


/**
 *
 * @author Wannes
 */
@RequestScoped
@Path("ritten")
@Transactional(dontRollbackOn = {BadRequestException.class, NotFoundException.class})
public class Ritten
{
    @PersistenceContext
    private EntityManager em;
    
    @Resource
    private Validator validator;
    
    @Context
    private SecurityContext context;
    
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Rit getRit(@PathParam("id") Long id)
    {
        Rit rit= em.find(Rit.class, id);
        
        if (rit == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }
        
        return rit;
    }
    
    
    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateRit(@PathParam("id") Long id, InputStream input)
    {
        Rit rit = em.find(Rit.class, id);
        
        if(!context.getUserPrincipal().getName().equals(rit.getUser().getName()) && !context.isUserInRole("admin") ){
            throw new ForbiddenException();
        }
        
        if (rit == null) {
            throw new NotFoundException("rit niet gevonden");
        }
        
        try (JsonReader jsonInput = Json.createReader(input)) {
            JsonObject jsonRit = jsonInput.readObject();
            
            String titel = jsonRit.getString("titel", null).trim();
            
            rit.setTitle(titel);
            
            long afstand = jsonRit.getInt("afstand", 0);
            
            rit.setAfstand(afstand);
            
            Set<ConstraintViolation<Rit>> fouten = validator.validate(rit);

        if (!fouten.isEmpty()) {

            fouten.stream().map(f -> f.getMessage()).forEach(System.err::println);

            throw new BadRequestException("ongeldige invoer");
        }
            
        } catch (JsonException | ClassCastException ex) {
            throw new BadRequestException("Ongeldige JSON invoer");
        }
        
    }
    
    @Path("{id}")
    @DELETE
    public void removeRit(@PathParam("id") Long id)
    {
        Rit rit = em.find(Rit.class, id);
        
        if(!context.getUserPrincipal().getName().equals(rit.getUser().getName()) && !context.isUserInRole("admin") ){
            throw new ForbiddenException();
        }
        
        if (rit == null) {
            throw new NotFoundException("rit niet gevonden");
        }
        
        rit.getUser().getRitten().remove(rit);
        
        em.remove(rit);
        //em.getEntityManagerFactory().getCache().evictAll();
        
    }
}
