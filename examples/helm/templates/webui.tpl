{{- define "teastore.webui.microservice" -}}
webui
{{- end }}

{{- define "teastore.webui.fullname" -}}
{{- include "teastore.fullname" . }}-{{- include "teastore.webui.microservice" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "teastore.webui.selectorLabels" -}}
{{ include "teastore.selectorLabels" . }}
app.kubernetes.io/name: {{ include "teastore.webui.microservice" . }}
app.kubernetes.io/component: webserver
{{- end }}

{{- define "teastore.webui.hostname" -}}
{{- if .Values.clientside_loadbalancer -}}
$(POD_NAME).{{- .Values.webui.svc_name -}}
{{- else if .Values.webui.url -}}
{{- .Values.webui.url -}}
{{- else -}}
{{- .Values.webui.svc_name -}}
{{- end -}}
{{- end -}}
