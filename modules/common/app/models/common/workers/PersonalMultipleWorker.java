package models.common.workers;

import com.fasterxml.jackson.annotation.JsonCreator;
import general.common.MessagesStrings;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import play.data.validation.ValidationError;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * DB entity of the concrete Worker for a Personal Multiple Run (worker for an external run that can be used multiple
 * times and always assigns the results to the same worker).
 *
 * @author Kristian Lange
 */
@Entity
@DiscriminatorValue(PersonalMultipleWorker.WORKER_TYPE)
public class PersonalMultipleWorker extends Worker {

    public static final String WORKER_TYPE = "PersonalMultiple";
    public static final String UI_WORKER_TYPE = "Personal Multiple";
    public static final String COMMENT = "comment";

    private String comment;

    public PersonalMultipleWorker() {
    }

    @JsonCreator
    public PersonalMultipleWorker(String comment) {
        this.comment = comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    @Override
    public String getWorkerType() {
        return WORKER_TYPE;
    }

    @Override
    public String getUIWorkerType() {
        return UI_WORKER_TYPE;
    }

    @Override
    public String generateConfirmationCode() {
        return null;
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errorList = new ArrayList<>();
        if (comment != null && comment.length() > 255) {
            errorList.add(new ValidationError(COMMENT,
                    MessagesStrings.COMMENT_TOO_LONG));
        }
        if (comment != null && !Jsoup.isValid(comment, Whitelist.none())) {
            errorList.add(new ValidationError(COMMENT,
                    MessagesStrings.NO_HTML_ALLOWED));
        }
        return errorList.isEmpty() ? null : errorList;
    }

}
