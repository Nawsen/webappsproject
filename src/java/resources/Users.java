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
@Path("users")
@Transactional(dontRollbackOn = {BadRequestException.class, NotFoundException.class})
public class Users {

    @PersistenceContext
    private EntityManager em;
    @Resource
    private Validator validator;
    @Context
    private SecurityContext context;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers(@QueryParam("first") @DefaultValue("0") int first, @QueryParam("results") @DefaultValue("10") int results) {
        TypedQuery<User> queryFindAll = em.createNamedQuery("User.findAll", User.class);
        queryFindAll.setFirstResult(first);
        queryFindAll.setMaxResults(results);
        return queryFindAll.getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        user.setName(user.getName().trim());

        user.setPassword(user.getPassword().trim());

        user.setEmail(user.getEmail().toLowerCase().trim());
        
        user.getRoles().add("user");
        
        Set<ConstraintViolation<User>> fouten = validator.validate(user);

        if (!fouten.isEmpty()) {

            fouten.stream().map(f -> f.getMessage()).forEach(System.err::println);

            throw new BadRequestException("ongeldige invoer");
        }

        em.persist(user);

        return Response.created(URI.create("/" + user.getName())).build();
    }

    @Path("{id}/ritten")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRit(@PathParam("id") Long id, Rit rit) {
        
        User user = em.find(User.class, id);

        if(!context.getUserPrincipal().getName().equals(user.getName()) && !context.isUserInRole("admin") ){
            throw new ForbiddenException();
        }
        
        if (user == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }

        rit.setTitle(rit.getTitle().trim());
        rit.setAfstand(rit.getAfstand());


        rit.setUser(user);
        user.addRit(rit);
        
        Set<ConstraintViolation<Rit>> fouten = validator.validate(rit);

        if (!fouten.isEmpty()) {

            fouten.stream().map(f -> f.getMessage()).forEach(System.err::println);

            throw new BadRequestException("ongeldige invoer");
        }
        
        em.persist(rit);
        em.persist(user);

        return Response.created(URI.create("/" + rit.getTitle())).build();
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") Long id) {
        User user = em.find(User.class, id);

        if (user == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }

        return user;
    }

    @Path("{id}/ritten")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Rit> getRitten(@PathParam("id") Long id) {
        User user = em.find(User.class, id);

        if(!context.getUserPrincipal().getName().equals(user.getName()) && !context.isUserInRole("admin") ){
            throw new ForbiddenException();
        }
        
        if (user == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }

        return user.getRitten();
    }
    
//    @Path("{name}/ritten")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Rit> getRitten(@PathParam("name") String name) {
//    User user = (User) em.createQuery("SELECT * FROM TBL_USER WHERE name = " + name);
//        if(!context.getUserPrincipal().getName().equals(user.getName()) && !context.isUserInRole("admin") ){
//            throw new ForbiddenException();
//        }
//        
//        if (user == null) {
//            throw new NotFoundException("Gebruiker niet gevonden");
//        }
//
//        return user.getRitten();
//    }

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateUser(@PathParam("id") Long id, InputStream input) {
        User user = em.find(User.class, id);

        if(!context.getUserPrincipal().getName().equals(user.getName()) && !context.isUserInRole("admin") ){
            throw new ForbiddenException();
        }
        
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
            user.setPassword(password.trim());
            String email = jsonUser.getString("email", null);
            user.setEmail(email.trim());

            Set<ConstraintViolation<User>> fouten = validator.validate(user);

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
    public void removeUser(@PathParam("id") Long id) {
        User user = em.find(User.class, id);
        if(!context.getUserPrincipal().getName().equals(user.getName()) && !context.isUserInRole("admin") ){
            throw new ForbiddenException();
        }
        if (user == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }

        em.remove(user);
    }
}
