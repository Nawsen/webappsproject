
package resources;

import domain.Rit;
import domain.User;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 *
 * @author Wannes
 */
@RequestScoped
@Path("users")
@Transactional(dontRollbackOn = {BadRequestException.class, NotFoundException.class})
public class Users
{
    @PersistenceContext
    private EntityManager em;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers(@QueryParam("first") @DefaultValue("0") int first, @QueryParam("results") @DefaultValue("10") int results)
    {
        TypedQuery<User> queryFindAll = em.createNamedQuery("User.findAll", User.class);
        queryFindAll.setFirstResult(first);
        queryFindAll.setMaxResults(results);
        return queryFindAll.getResultList();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user)
    {
        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim());
        user.setPassword(user.getPassword().trim());
        
        if (user.getName().length() < 5) {
            throw new BadRequestException("Username ongeldig");
        }
        
        if (em.find(User.class, user.getId()) != null) {
            throw new BadRequestException("Username al in gebruik");
        }
        
        if (user.getPassword().length() < 5) {
            throw new BadRequestException("Paswoord ongeldig");
        }
        
        
        user.setPassword(user.getPassword().trim());
        
        user.setEmail(user.getEmail().toLowerCase().trim());
        
        em.persist(user);
        
        return Response.created(URI.create("/" + user.getName())).build();
    }
    
    @Path("{id}/ritten")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRit(@PathParam("id") Long id, Rit rit)
    {
        
        User user = em.find(User.class, id);
        
        if (user == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }
        
        rit.setTitle(rit.getTitle().trim());
        rit.setAfstand(rit.getAfstand());
        
        if (rit.getTitle().length() < 3){
            throw new BadRequestException("titel te kort");
        }
        rit.setUser(user);
        user.addRit(rit);
        em.persist(rit);
        em.persist(user);
        
        return Response.created(URI.create("/" + rit.getTitle())).build();
    }
    
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") Long id)
    {
        User user = em.find(User.class, id);
        
        if (user == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }
        
        return user;
    }
    
     @Path("{id}/ritten")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Rit> getRitten(@PathParam("id") Long id)
    {
        User user = em.find(User.class, id);
                
        if (user == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }
        
        return user.getRitten();
    }
    
    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateUser(@PathParam("id") Long id, InputStream input)
    {
        User user = em.find(User.class, id);
        
        if (user == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }
        
        try (JsonReader jsonInput = Json.createReader(input)) {
            JsonObject jsonUser = jsonInput.readObject();

            // Ter illustratie ondersteunen we hier enkel het wijzigen van het paswoord en de
            // fullName. Hoe je een volledige update kan ondersteunen, is te vinden in het grote
            // voorbeeld 'Reminders'.
            
            String name = jsonUser.getString("name", null);
            user.setName(name);
            
            String password = jsonUser.getString("password", null);
            if (password != null) {
                if (password.trim().length() < 8) {
                    throw new BadRequestException("Paswoord ongeldig");
                } else {
                    user.setPassword(password.trim());
                }
            }

            String email = jsonUser.getString("email", null);
            if (email != null) {
                user.setEmail(email.trim());
            }

        } catch (JsonException | ClassCastException ex) {
            throw new BadRequestException("Ongeldige JSON invoer");
        }
    }
    
    @Path("{id}")
    @DELETE
    public void removeUser(@PathParam("id") Long id)
    {
        User user = em.find(User.class, id);
        
        if (user == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }
        
        em.remove(user);
    }
}
