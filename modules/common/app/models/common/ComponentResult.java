package models.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

/**
 * DB entity of a component result. It's used by JPA and JSON
 * marshaling.
 *
 * @author Kristian Lange
 */
@Entity
@Table(name = "ComponentResult")
@JsonPropertyOrder(value = { "id", "startDate", "workerId", "workerType", "componentState" })
public class ComponentResult {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Time and date when the component was started on the server.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    private Timestamp startDate;

    /**
     * Time and date when the component was finished on the server.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    private Timestamp endDate;


    /**
     * State of this component run (it actually should be called ComponentResultState)
     */
    public enum ComponentState {
        STARTED, // Component was started
        DATA_RETRIEVED, // Component's jsonData were retrieved
        RESULTDATA_POSTED, // Not used anymore but kept to ensure proper JPA enum mapping
        FINISHED, // Component was finished
        RELOADED, // Component was reloaded
        ABORTED, // Component aborted by worker
        FAIL; // Something went wrong
        public static String allStatesAsString() {
            String str = Arrays.toString(values());
            return str.substring(1, str.length() - 1);
        }
    }

    /**
     * State in the progress of a component result. (Yes, it should be named
     * componentResultState - but hey, it's so much nicer this way.)
     */
    private ComponentState componentState;

    /**
     * The Component corresponding to this ComponentResult. This relationship is
     * unidirectional.
     */
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id")
    private Component component;

    /**
     * StudyResult that this ComponentResult belongs to. This relationship is
     * bidirectional.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyResult_id", insertable = false, updatable = false, nullable = false)
    private StudyResult studyResult;

    /**
     * Result data string submitted from the client during running the
     * component. It can be any string and doesn't have to be in JSON format.
     */
    @Lob
    @JsonIgnore
    private String data;

    /**
     * Some message usually set via jatos.startComponent. Max 255 characters. Can be left null.
     */
    private String message;

    public ComponentResult() {
    }

    public ComponentResult(Component component) {
        this.startDate = new Timestamp(new Date().getTime());
        this.component = component;
        this.componentState = ComponentState.STARTED;
    }

    @JsonProperty("workerId")
    public Long getWorkerId() {
        return studyResult.getWorker().getId();
    }

    @JsonProperty("workerType")
    public String getWorkerType() {
        return studyResult.getWorker().getWorkerType();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getStartDate() {
        return this.startDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Timestamp getEndDate() {
        return this.endDate;
    }

    public void setComponentState(ComponentState state) {
        this.componentState = state;
    }

    public ComponentState getComponentState() {
        return this.componentState;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return this.component;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setMessage(String message) {
        this.message = StringUtils.substring(message, 0, 255);
    }

    public String getMessage() {
        return this.message;
    }

    public void setStudyResult(StudyResult studyResult) {
        this.studyResult = studyResult;
    }

    public StudyResult getStudyResult() {
        return this.studyResult;
    }

    @Override
    public String toString() {
        return id + ", " + startDate + ", " + component.getId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null) return false;

        if (!(obj instanceof ComponentResult)) return false;

        ComponentResult other = (ComponentResult) obj;
        if (getId() == null) return other.getId() == null;
        return getId().equals(other.getId());
    }

}
