package password.pwm.http.servlet.configeditor;

import password.pwm.PwmConstants;
import password.pwm.config.value.FileValue;
import password.pwm.error.ErrorInformation;
import password.pwm.error.PwmError;
import password.pwm.error.PwmException;
import password.pwm.error.PwmUnrecoverableException;
import password.pwm.http.PwmRequest;
import password.pwm.util.logging.PwmLogger;
import password.pwm.ws.server.RestResultBean;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigEditorServletUtils
{

    private static final PwmLogger LOGGER = PwmLogger.forClass( ConfigEditorServletUtils.class );


    public static FileValue readFileUploadToSettingValue(
            final PwmRequest pwmRequest,
            final int maxFileSize
    )
            throws PwmUnrecoverableException, IOException, ServletException
    {

        final Map<String, PwmRequest.FileUploadItem> fileUploads;
        try
        {
            fileUploads = pwmRequest.readFileUploads( maxFileSize, 1 );
        }
        catch ( PwmException e )
        {
            pwmRequest.outputJsonResult( RestResultBean.fromError( e.getErrorInformation(), pwmRequest ) );
            LOGGER.error( pwmRequest, "error during file upload: " + e.getErrorInformation().toDebugStr() );
            return null;
        }
        catch ( Throwable e )
        {
            final ErrorInformation errorInformation = new ErrorInformation( PwmError.ERROR_UNKNOWN, "error during file upload: " + e.getMessage() );
            pwmRequest.outputJsonResult( RestResultBean.fromError( errorInformation, pwmRequest ) );
            LOGGER.error( pwmRequest, errorInformation );
            return null;
        }

        if ( fileUploads.containsKey( PwmConstants.PARAM_FILE_UPLOAD ) )
        {
            final PwmRequest.FileUploadItem uploadItem = fileUploads.get( PwmConstants.PARAM_FILE_UPLOAD );

            final Map<FileValue.FileInformation, FileValue.FileContent> newFileValueMap = new LinkedHashMap<>();
            newFileValueMap.put( new FileValue.FileInformation( uploadItem.getName(), uploadItem.getType() ), new FileValue.FileContent( uploadItem.getContent() ) );

            return new FileValue( newFileValueMap );
        }

        final ErrorInformation errorInformation = new ErrorInformation( PwmError.ERROR_UNKNOWN, "no file found in upload" );
        pwmRequest.outputJsonResult( RestResultBean.fromError( errorInformation, pwmRequest ) );
        LOGGER.error( pwmRequest, "error during file upload: " + errorInformation.toDebugStr() );
        return null;
    }

}
