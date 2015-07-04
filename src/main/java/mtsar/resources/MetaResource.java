package mtsar.resources;

import mtsar.MechanicalTsarVersion;
import mtsar.api.Process;
import mtsar.api.sql.AnswerDAO;
import mtsar.api.sql.TaskDAO;
import mtsar.api.sql.WorkerDAO;
import mtsar.views.DashboardView;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Singleton
@Path("/")
@Produces(mtsar.MediaType.APPLICATION_JSON)
public class MetaResource {
    protected final MechanicalTsarVersion version;
    protected final Map<String, Process> processes;
    protected final TaskDAO taskDAO;
    protected final WorkerDAO workerDAO;
    protected final AnswerDAO answerDAO;

    @Inject
    public MetaResource(MechanicalTsarVersion version, @Named("processes") Map<String, Process> processes, TaskDAO taskDAO, WorkerDAO workerDAO, AnswerDAO answerDAO) {
        this.version = version;
        this.processes = processes;
        this.taskDAO = taskDAO;
        this.workerDAO = workerDAO;
        this.answerDAO = answerDAO;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public DashboardView getDashboardView() {
        return new DashboardView(version, processes, taskDAO, workerDAO, answerDAO);
    }

    @GET
    @Path("version")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVersion() {
        return version.getVersion();
    }
}
