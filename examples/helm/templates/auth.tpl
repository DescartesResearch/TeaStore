{{- define "teastore.auth.microservice" -}}
auth
{{- end }}

{{- define "teastore.auth.fullname" -}}
{{- include "teastore.fullname" . }}-{{- include "teastore.auth.microservice" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "teastore.auth.selectorLabels" -}}
{{ include "teastore.selectorLabels" . }}
app.kubernetes.io/name: {{ include "teastore.auth.microservice" . }}
app.kubernetes.io/component: authenticator
{{- end }}

{{- define "teastore.auth.hostname" -}}
{{- if .Values.clientside_loadbalancer -}}
$(POD_NAME).{{- .Values.auth.svc_name -}}
{{- else if .Values.auth.url -}}
{{- .Values.auth.url -}}
{{- else -}}
{{- .Values.auth.svc_name -}}
{{- end -}}
{{- end -}}
